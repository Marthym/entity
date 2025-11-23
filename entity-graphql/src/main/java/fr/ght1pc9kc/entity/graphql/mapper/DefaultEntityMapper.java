package fr.ght1pc9kc.entity.graphql.mapper;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import fr.ght1pc9kc.entity.api.mapper.EntityMapper;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link EntityMapper} using standard Java Reflection.
 * <p>
 * This mapper converts an {@link Entity} into a {@link Map} by inspecting the object's structure.
 * It supports reading properties from:
 * <ul>
 *     <li>Public fields</li>
 *     <li>JavaBean compliant getters</li>
 *     <li>Java Records components</li>
 * </ul>
 * <p>
 * It automatically handles recursive mapping of nested objects, {@link Collection}, and Arrays.
 * <p>
 * The resulting Map includes:
 * <ul>
 *     <li>The entity identifier under the key {@value #DEFAULT_IDENTIFIER_PROPERTY}</li>
 *     <li>All metadata (if available) prefixed with {@value #META_PREFIX}</li>
 *     <li>All properties of the wrapped object (self)</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * public class Jedi {
 *     public final String name;
 *     public final String faction;
 *     private final String weapon;
 *
 *     public Jedi(String name, String faction, String weapon) {
 *         this.name = name;
 *         this.faction = faction;
 *         this.weapon = weapon;
 *     }
 *
 *     public String getWeapon() { return weapon; }
 * }
 *
 * Entity<Jedi> entity = Entity.identify(new Jedi("Obiwan", "Jedi", "Lightsaber"))
 *         .withId("42")
 *         .meta("createdBy", "Lucas");
 *
 * EntityMapper mapper = new DefaultEntityMapper();
 * Map<String, Object> map = mapper.toMap(entity);
 *
 * // Resulting Map:
 * // {
 * //   "_id": "42",
 * //   "_createdBy": "Lucas",
 * //   "name": "Obiwan",
 * //   "faction": "Jedi",
 * //   "weapon": "Lightsaber"
 * // }
 * }</pre>
 *
 * <p><strong>Note on Visibility:</strong></p>
 * <p>
 * This mapper respects Java access control. It only reads <strong>public</strong> fields,
 * <strong>public</strong> getters, or record components.
 * Private fields without public getters are ignored to comply with Java module encapsulation rules.
 * </p>
 */
public class DefaultEntityMapper implements EntityMapper {
    public static final String META_PREFIX = "_";
    private static final String DEFAULT_IDENTIFIER_PROPERTY = "_id";

    @Override
    public String entityIdentifierProperty() {
        return DEFAULT_IDENTIFIER_PROPERTY;
    }

    @Override
    public Map<String, Object> toMap(Entity<?> entity) {
        if (entity == null) {
            return Map.of();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(DEFAULT_IDENTIFIER_PROPERTY, entity.id());
        if (entity instanceof ExtendedEntity<?, ?> extendedEntity) {
            extendedEntity.metas().forEach((metaName, metaValue) ->
                    map.put(META_PREFIX + metaName, metaValue));
        }

        Object self = entity.self();
        if (self != null) {
            map.putAll(objectToMap(self));
        }

        return Map.copyOf(map);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return Map.of();
        }

        if (obj instanceof Map) {
            Map<String, Object> result = new HashMap<>();
            ((Map<String, Object>) obj).forEach((k, v) -> result.put(k, convertValue(v)));
            return result;
        }

        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();

        if (clazz.isRecord()) {
            map.putAll(readRecord(obj));
        } else {
            map.putAll(readPOJO(obj));
        }

        return Map.copyOf(map);
    }

    private Map<String, Object> readRecord(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        try {
            if (clazz.isRecord()) {
                for (RecordComponent rc : clazz.getRecordComponents()) {
                    Method accessor = rc.getAccessor();
                    Object value = accessor.invoke(obj);
                    map.put(rc.getName(), convertValue(value));
                }
            }
        } catch (Exception e) {
            errorHandler(e, clazz);
        }
        return Map.copyOf(map);
    }

    private Map<String, Object> readPOJO(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        try {
            for (Field field : clazz.getFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                    Object value = field.get(obj);
                    map.put(field.getName(), convertValue(value));
                }
            }

            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                Method readMethod = pd.getReadMethod();

                if (readMethod != null && !"class".equals(pd.getName())) {
                    Object value = readMethod.invoke(obj);
                    map.put(pd.getName(), convertValue(value));
                }
            }
        } catch (Exception e) {
            errorHandler(e, clazz);
        }
        return Map.copyOf(map);
    }

    private Object convertValue(Object value) {
        if (value == null) {
            return null;
        }

        if (isSimpleType(value.getClass())) {
            return value;
        }

        if (value instanceof Collection<?> collValue) {
            List<Object> list = new ArrayList<>(collValue.size());
            for (Object item : collValue) {
                list.add(convertValue(item));
            }
            return list;
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(convertValue(Array.get(value, i)));
            }
            return list;
        }

        return objectToMap(value);
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive() ||
                String.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) ||
                Boolean.class.isAssignableFrom(clazz) ||
                Character.class.isAssignableFrom(clazz) ||
                java.time.temporal.Temporal.class.isAssignableFrom(clazz) ||
                java.util.Date.class.isAssignableFrom(clazz) ||
                clazz.isEnum();
    }
}

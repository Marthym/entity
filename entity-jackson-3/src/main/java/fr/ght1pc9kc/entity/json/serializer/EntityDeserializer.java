package fr.ght1pc9kc.entity.json.serializer;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.TypedMeta;
import fr.ght1pc9kc.entity.api.impl.BasicEntity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import fr.ght1pc9kc.entity.json.EntityModuleConstant;
import fr.ght1pc9kc.entity.json.ex.EntityDeserializationException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ObjectNode;

import java.util.EnumMap;
import java.util.Optional;

/**
 * <p>Manage the {@link Entity}, {@link BasicEntity} and {@link ExtendedEntity} deserialization.</p>
 * <p>All field starting with "{@code _}" are considered as meta data and will be used for Entity,
 * all other fields are considered to belong to the self object</p>
 *
 * <p>{@link Entity} and {@link BasicEntity} are always deserialize as {@link BasicEntity}. To get a
 * {@link ExtendedEntity} with meta it is necessary to explicitly specify</p>
 * <pre>{@code
 * ObjectMapper mapper = new ObjectMapper();
 * Entity<SelfObject> entity = mapper.readValue(json, new TypeReference<ExtendedEntity<SelfObject, MetaEnum>>() {});
 * }</pre>
 *
 * @param <T> The type of {@code self} object in {@link Entity}
 */
public class EntityDeserializer<T> extends ValueDeserializer<Entity<T>> {
    private final ValueDeserializer<Object> selfDeserializer;
    private final JavaType metaEnum;

    public EntityDeserializer(JavaType metaEnum, ValueDeserializer<Object> selfDeserializer) {
        this.selfDeserializer = selfDeserializer;
        this.metaEnum = metaEnum;
    }

    /**
     * @param jp   Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *             this deserialization activity.
     * @return The serialized {@link BasicEntity} or {@link ExtendedEntity} depending on the context
     * @throws EntityDeserializationException when {@code _id} is absent
     */
    @Override
    @SuppressWarnings("unchecked")
    public Entity<T> deserialize(JsonParser jp, DeserializationContext ctxt) {
        JsonToken currentToken = jp.currentToken();

        if (currentToken != JsonToken.START_OBJECT) {
            throw ctxt.wrongTokenException(jp, (JavaType) null, JsonToken.START_OBJECT, "Bad token");
        }

        ObjectNode treeNode = jp.readValueAsTree();
        ObjectNode entityNode = ctxt.getNodeFactory().objectNode();
        ObjectNode wrappedNode = ctxt.getNodeFactory().objectNode();

        treeNode.properties().forEach(entry -> {
            if (entry.getKey().startsWith(EntityModuleConstant.META_PREFIX)) {
                entityNode.set(entry.getKey(), entry.getValue());
            } else {
                wrappedNode.set(entry.getKey(), entry.getValue());
            }
        });

        String id = Optional.ofNullable(treeNode.remove(EntityModuleConstant.IDENTIFIER))
                .map(JsonNode::asString)
                .orElseThrow(() -> new EntityDeserializationException(EntityModuleConstant.IDENTIFIER + " is mandatory for Entity"));

        JsonNode selfNode = entityNode.get(EntityModuleConstant.SELF_PROPERTY);
        JsonNode selfContainedNode = (selfNode == null) ? wrappedNode : selfNode;

        JsonParser subParser = selfContainedNode.traverse(jp.objectReadContext());
        subParser.nextToken();
        T wrapped = (T) selfDeserializer.deserialize(subParser, ctxt);

        if (metaEnum != null && metaEnum.isEnumType()) {
            return readExtendedEntity(jp, entityNode, id, wrapped);
        }
        return new BasicEntity<>(id, wrapped);
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>> Entity<T> readExtendedEntity(JsonParser jp, ObjectNode entityNode, String id, T self) {
        EnumMap<E, Object> metas = new EnumMap<>((Class<E>) metaEnum.getRawClass());

        boolean isTyped = TypedMeta.class.isAssignableFrom(metaEnum.getRawClass());

        for (Object enumConstant : metaEnum.getRawClass().getEnumConstants()) {
            E key = (E) enumConstant;
            String fieldName = ((Enum<?>) enumConstant).name();
            JsonNode fieldValue = entityNode.get(EntityModuleConstant.META_PREFIX + fieldName);
            if (fieldValue != null) {
                if (isTyped) {
                    Class<?> clazz = ((TypedMeta) enumConstant).type();
                    try (JsonParser valueParser = fieldValue.traverse(jp.objectReadContext())) {
                        Object value = valueParser.readValueAs(clazz);
                        metas.put(key, value);
                    }
                } else {
                    metas.put(key, fieldValue.asString());
                }
            }
        }

        return new ExtendedEntity<>(id, metas, self);
    }
}
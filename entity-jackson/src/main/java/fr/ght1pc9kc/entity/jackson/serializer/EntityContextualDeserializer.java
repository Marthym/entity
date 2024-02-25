package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import fr.ght1pc9kc.entity.jackson.ex.EntityDeserializationException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityContextualDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    private final Map<JavaType, JsonDeserializer<Object>> deserCache = new ConcurrentHashMap<>();

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        JavaType selfType = (property == null)
                ? context.getContextualType().containedType(0) : property.getType().containedType(0);
        JavaType enumType = (property == null)
                ? context.getContextualType().containedType(1) : property.getType().containedType(1);

        JsonDeserializer<Object> selfJsonDeserializer = deserCache.computeIfAbsent(selfType, type -> {
            try {
                return context.findRootValueDeserializer(type);
            } catch (JsonMappingException e) {
                throw new EntityDeserializationException("Unable to find deserializer for type " + type, e);
            }
        });

        return new EntityDeserializer<>(enumType, selfJsonDeserializer);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) {
        return null;
    }
}

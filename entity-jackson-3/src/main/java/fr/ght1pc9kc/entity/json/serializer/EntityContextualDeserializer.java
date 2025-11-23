package fr.ght1pc9kc.entity.json.serializer;


import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityContextualDeserializer extends ValueDeserializer<Object> {
    private final Map<JavaType, ValueDeserializer<Object>> deserCache = new ConcurrentHashMap<>();

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        JavaType selfType = (property == null)
                ? context.getContextualType().containedType(0) : property.getType().containedType(0);
        JavaType enumType = (property == null)
                ? context.getContextualType().containedType(1) : property.getType().containedType(1);

        ValueDeserializer<Object> selfJsonDeserializer = deserCache.computeIfAbsent(selfType, context::findRootValueDeserializer);

        return new EntityDeserializer<>(enumType, selfJsonDeserializer);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) {
        return null;
    }
}

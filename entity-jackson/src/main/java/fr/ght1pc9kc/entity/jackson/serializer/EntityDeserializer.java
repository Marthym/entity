package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.ght1pc9kc.entity.api.Entity;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EntityDeserializer<T> extends JsonDeserializer<Entity<T>> implements ContextualDeserializer {
    private final Map<JavaType, JsonDeserializer<Object>> deserCache = new ConcurrentHashMap<>();
    private JsonDeserializer<Object> wrappedDeserializer;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        EntityDeserializer<T> deserializer = new EntityDeserializer<T>();
        JavaType paramType = (property == null)
                ? context.getContextualType().containedType(0) : property.getType().containedType(0);

        deserializer.wrappedDeserializer = deserCache.computeIfAbsent(paramType, type -> {
            try {
                return context.findRootValueDeserializer(type);
            } catch (JsonMappingException e) {
                throw new IllegalStateException(e);
            }
        });

        return deserializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Entity<T> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken != JsonToken.START_OBJECT) {
            throw ctxt.wrongTokenException(jp, (JavaType) null, JsonToken.START_OBJECT, "Bad token");
        }

        ObjectNode treeNode = jp.readValueAsTree();
        ObjectNode entityNode = ctxt.getNodeFactory().objectNode();
        ObjectNode wrappedNode = ctxt.getNodeFactory().objectNode();

        treeNode.fields().forEachRemaining(entry -> {
            if (entry.getKey().startsWith("_")) {
                entityNode.set(entry.getKey(), entry.getValue());
            } else {
                wrappedNode.set(entry.getKey(), entry.getValue());
            }
        });

        String id = Optional.ofNullable(treeNode.remove(Entity.IDENTIFIER))
                .map(JsonNode::asText)
                .orElseThrow(IllegalArgumentException::new);
        String createdBy = Optional.ofNullable(treeNode.remove(Entity.CREATED_BY))
                .map(JsonNode::asText)
                .orElse(Entity.NO_ONE);
        Instant createdAt = Optional.ofNullable(treeNode.remove(Entity.CREATED_AT))
                .map(JsonNode::asText).map(Instant::parse)
                .orElseThrow(IllegalArgumentException::new);

        JsonParser subParser = wrappedNode.traverse(jp.getCodec());
        subParser.nextToken();
        T wrapped = (T) wrappedDeserializer.deserialize(subParser, ctxt);

        return new Entity<>(id, createdBy, createdAt, wrapped);
    }
}
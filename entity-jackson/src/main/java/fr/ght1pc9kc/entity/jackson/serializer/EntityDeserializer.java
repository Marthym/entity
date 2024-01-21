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
import fr.ght1pc9kc.entity.jackson.ex.EntityDeserializationException;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Manage the {@link Entity} deserialization.</p>
 * <p>All field starting with "{@code _}" are considered as meta data and will be used for Entity,
 * all other fields are considered to belong to the self object</p>
 * <p>{@code _id}, {@code _createdAt} and {@code _createdBy} are mandatory in meta data if absent:
 * <ul>
 *     <li>{@code _id}: throw {@link EntityDeserializationException}</li>
 *     <li>{@code _createdAt}: fill with {@link Instant#EPOCH}</li>
 *     <li>{@code _createdBy}: fill with {@link Entity#NO_ONE}</li>
 * </ul></p>
 *
 * @param <T> The type of {@code self} object in {@link Entity}
 */
public class EntityDeserializer<T> extends JsonDeserializer<Entity<T>> implements ContextualDeserializer {
    private final Map<JavaType, JsonDeserializer<Object>> deserCache = new ConcurrentHashMap<>();
    private JsonDeserializer<Object> wrappedDeserializer;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        EntityDeserializer<T> deserializer = new EntityDeserializer<>();
        JavaType paramType = (property == null)
                ? context.getContextualType().containedType(0) : property.getType().containedType(0);

        deserializer.wrappedDeserializer = deserCache.computeIfAbsent(paramType, type -> {
            try {
                return context.findRootValueDeserializer(type);
            } catch (JsonMappingException e) {
                throw new EntityDeserializationException("Unable to find deserializer for type " + type, e);
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
                .orElseThrow(() -> new EntityDeserializationException(Entity.IDENTIFIER + " is mandatory for Entity"));
        String createdBy = Optional.ofNullable(treeNode.remove(Entity.CREATED_BY))
                .map(JsonNode::asText)
                .orElse(Entity.NO_ONE);
        Instant createdAt = Optional.ofNullable(treeNode.remove(Entity.CREATED_AT))
                .map(JsonNode::asText).map(Instant::parse)
                .orElse(Instant.EPOCH);

        JsonNode selfNode = entityNode.get(Entity.SELF);
        JsonNode selfContainedNode = (selfNode == null) ? wrappedNode : selfNode;

        JsonParser subParser = selfContainedNode.traverse(jp.getCodec());
        subParser.nextToken();
        T wrapped = (T) wrappedDeserializer.deserialize(subParser, ctxt);

        return new Entity<>(id, createdBy, createdAt, wrapped);
    }
}
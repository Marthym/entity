package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.jackson.ex.EntitySerializationException;

import java.io.IOException;
import java.time.Instant;

public class EntitySerializer<T> extends JsonSerializer<Entity<T>> {
    private static final Entity<?> DUMMY = new Entity<>("", Entity.NO_ONE, Instant.EPOCH, new Object());

    @Override
    @SuppressWarnings("unchecked")
    public Class<Entity<T>> handledType() {
        return (Class<Entity<T>>) DUMMY.getClass();
    }

    @Override
    public void serialize(Entity<T> value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(Entity.IDENTIFIER, value.id());
        jgen.writeFieldName(Entity.CREATED_AT);
        jgen.writeObject(value.createdAt());
        jgen.writeStringField(Entity.CREATED_BY, value.createdBy());
        JsonNode self = ((ObjectMapper) jgen.getCodec()).valueToTree(value.self());
        if (self.isObject()) {
            self.fields().forEachRemaining(e -> {
                try {
                    jgen.writeObjectField(e.getKey(), e.getValue());
                } catch (IOException ex) {
                    throw new EntitySerializationException("Unable to write Entity self sub object key " + e.getKey(), ex);
                }
            });
        } else {
            jgen.writePOJOField(Entity.SELF_PROPERTY, self);
        }

        jgen.writeEndObject();
    }
}

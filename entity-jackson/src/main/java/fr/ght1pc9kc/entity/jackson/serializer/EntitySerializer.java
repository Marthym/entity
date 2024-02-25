package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import fr.ght1pc9kc.entity.jackson.ex.EntitySerializationException;

import java.io.IOException;

public class EntitySerializer<T> extends StdSerializer<Entity<T>> {
    public EntitySerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serialize(Entity<T> value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(EntityModuleConstant.IDENTIFIER, value.id());
        if (value instanceof ExtendedEntity<T, ?> extendedEntity) {
            extendedEntity.metas().forEach((metaName, metaValue) -> {
                try {
                    jgen.writeFieldName(EntityModuleConstant.META_PREFIX + metaName);
                    jgen.writeObject(metaValue);
                } catch (IOException e) {
                    throw new EntitySerializationException("Unable to serialize meta " + metaName, e);
                }
            });
        }

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
            jgen.writePOJOField(EntityModuleConstant.SELF_PROPERTY, self);
        }

        jgen.writeEndObject();
    }
}

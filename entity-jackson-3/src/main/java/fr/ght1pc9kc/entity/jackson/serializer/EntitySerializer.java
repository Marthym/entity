package fr.ght1pc9kc.entity.jackson.serializer;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import fr.ght1pc9kc.entity.jackson.EntityModuleConstant;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class EntitySerializer<T> extends StdSerializer<Entity<T>> {
    public EntitySerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serialize(Entity<T> value, JsonGenerator jgen, SerializationContext context) {
        jgen.writeStartObject();
        jgen.writeStringProperty(EntityModuleConstant.IDENTIFIER, value.id());
        if (value instanceof ExtendedEntity<T, ?> extendedEntity) {
            extendedEntity.metas().forEach((metaName, metaValue) ->
                    jgen.writePOJOProperty(EntityModuleConstant.META_PREFIX + metaName, metaValue));
        }

        JsonNode self = context.valueToTree(value.self());
        if (self.isObject()) {
            self.properties().forEach(e -> jgen.writePOJOProperty(e.getKey(), e.getValue()));
        } else {
            jgen.writePOJOProperty(EntityModuleConstant.SELF_PROPERTY, self);
        }

        jgen.writeEndObject();
    }
}

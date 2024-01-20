package fr.ght1pc9kc.entity.jackson;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.jackson.serializer.EntityDeserializer;
import fr.ght1pc9kc.entity.jackson.serializer.EntitySerializer;

import java.util.List;
import java.util.Map;

public class EntityModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addDeserializers(new SimpleDeserializers(Map.of(
                Entity.class, new EntityDeserializer<>()
        )));
        context.addSerializers(new SimpleSerializers(List.of(
                new EntitySerializer<>()
        )));
    }
}

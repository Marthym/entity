package fr.ght1pc9kc.entity.json;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.impl.BasicEntity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import fr.ght1pc9kc.entity.json.serializer.EntityContextualDeserializer;
import fr.ght1pc9kc.entity.json.serializer.EntitySerializer;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tools.jackson.core.Version;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.util.VersionUtil;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleSerializers;

import java.util.List;
import java.util.Map;

/**
 * The Jackson module managing {@link Entity} type object
 *
 * <h2>Deserialization</h2>
 * <p>Manage the {@link Entity} deserialization.</p>
 * <p>All fields starting with "{@code _}" are considered as meta data and will be used for Entity,
 * all other fields are considered to belong to the self object</p>
 *
 * <h2>Serialization</h2>
 * <p>{@link Entity#self()} was serialized as <strong>unwrapped</strong> object. All properties from Entity were considered
 * as meta-data and were prefixed with {@code _}, properties from self object was serialized at the same leve "as is".</p>
 * <p>All Jackson annotations on the {@code self} object was read and used.</p>
 * <p>This serializer avoid Jackson problems of using {@link com.fasterxml.jackson.annotation.JsonTypeInfo} inside an
 * object annotated with {@link com.fasterxml.jackson.annotation.JsonUnwrapped}</p>
 * <p>{@see <a href="https://github.com/FasterXML/jackson-databind/issues/81">FasterXML/jackson-databind#81</a>}</p>
 *
 * <p>The entity :</p>
 * <pre>{@code
 *    Entity.<Saber>identify(new LightSaber(Color.GREEN, 1))
 *          .meta(createdAt, Instant.parse("2024-01-20T15:06:42.546Z"))
 *          .meta(createdBy, "okenobi")
 *          .withId("LIGHTSABER");
 * }</pre>
 * <p>become</p>
 * <pre>
 *     {
 *          "_id": "LIGHTSABER",
 *          "_createdAt": "2024-01-20T15:06:42.546Z",
 *          "_createdBy": "okenobi",
 *          "@type": "LIGHT",
 *          "blade": 1,
 *          "color": "GREEN"
 *     }
 * </pre>
 * <p>if Saber interface declared as </p>
 * <pre>{@code
 * @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
 * @JsonSubTypes({
 *      @JsonSubTypes.Type(value = LightSaber.class, name = "LIGHT"),
 *      @JsonSubTypes.Type(value = DarkSaber.class, name = "DARK")
 * })
 * public sealed interface Saber permits DarkSaber, LightSaber {
 *      Color color();
 * }
 * }</pre>
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EntityModule extends JacksonModule {
    @Override
    public String getModuleName() {
        return EntityModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return VersionUtil.parseVersion("1.1.0", EntityModule.class.getPackageName(), "jackson-entity-module");
    }

    @Override
    public void setupModule(SetupContext context) {
        EntityContextualDeserializer deserializer = new EntityContextualDeserializer();
        context.addDeserializers(new SimpleDeserializers(Map.of(
                Entity.class, deserializer,
                BasicEntity.class, deserializer,
                ExtendedEntity.class, deserializer
        )));
        context.addSerializers(new SimpleSerializers(List.of(
                new EntitySerializer<>(context.typeFactory().constructType(new TypeReference<Entity<?>>() {
                })),
                new EntitySerializer<>(context.typeFactory().constructType(new TypeReference<BasicEntity<?>>() {
                })),
                new EntitySerializer<>(context.typeFactory().constructType(new TypeReference<ExtendedEntity<?, ? extends Enum<?>>>() {
                }))
        )));
    }
}

package fr.ght1pc9kc.entity.jackson;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.jackson.serializer.EntityDeserializer;
import fr.ght1pc9kc.entity.jackson.serializer.EntitySerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * The Jackson module managing {@link Entity} type object
 *
 * <h2>Deserialization</h2>
 * <p>Manage the {@link Entity} deserialization.</p>
 * <p>All field starting with "{@code _}" are considered as meta data and will be used for Entity,
 * all other fields are considered to belong to the self object</p>
 * <p>{@code _id}, {@code _createdAt} and {@code _createdBy} are mandatory in meta data if absent:</p>
 * <ul>
 *     <li>{@code _id}: throw {@link fr.ght1pc9kc.entity.jackson.ex.EntityDeserializationException}</li>
 *     <li>{@code _createdAt}: fill with {@link java.time.Instant#EPOCH}</li>
 *     <li>{@code _createdBy}: fill with {@link Entity#NO_ONE}</li>
 * </ul>
 *
 * <h2>Serialization</h2>
 * <p>{@link Entity#self()} was serialized as <strong>unwrapped</strong> object. All properties from Entity was considered
 * as meta data and was prefixed with {@code _}, properties from self object was serialized at the same leve "as is".</p>
 * <p>All Jackson annotations on the {@code self} object was read and used.</p>
 * <p>This serializer avoid Jackson problem of using {@link com.fasterxml.jackson.annotation.JsonTypeInfo} inside an
 * object annotated with {@link com.fasterxml.jackson.annotation.JsonUnwrapped}</p>
 * <p>{@see <a href="https://github.com/FasterXML/jackson-databind/issues/81">FasterXML/jackson-databind#81</a>}</p>
 *
 * <p>The entity :</p>
 * <pre>{@code
 *    Entity.<Saber>builder()
 *             .id("LIGHTSABER")
 *             .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
 *             .createdBy("okenobi")
 *             .self(new LightSaber(Color.GREEN, 1))
 *             .build()
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
@Slf4j
public class EntityModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        log.info("Configure Jackson Entity module ...");
        context.addDeserializers(new SimpleDeserializers(Map.of(
                Entity.class, new EntityDeserializer<>()
        )));
        context.addSerializers(new SimpleSerializers(List.of(
                new EntitySerializer<>()
        )));
    }
}

package fr.ght1pc9kc.entity.jackson;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Implementation of {@link EntityMapper} based on Jackson 3.
 * <p>
 * This mapper leverages a Jackson {@link ObjectMapper} to convert an {@link Entity} into a {@link Map}.
 * It relies on the registered Jackson modules and configuration to handle serialization details,
 * ensuring consistency with the rest of the application's JSON handling.
 * </p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Delegates conversion to Jackson, respecting all annotations (e.g., {@code @JsonProperty}, {@code @JsonIgnore}).</li>
 *     <li>Supports complex nested structures and polymorphism if configured in the ObjectMapper.</li>
 *     <li>Uses {@link EntityModuleConstant#IDENTIFIER} as the identifier property name.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * // Assuming an ObjectMapper configured with appropriate modules
 * JsonMapper.builder()
 *         .addModule(new EntityModule())
 *         .build();
 * objectMapper.registerModule(new EntityModule()); // Required to handle Entity<?> structure
 *
 * Jackson2EntityMapper mapper = new Jackson2EntityMapper(objectMapper);
 *
 * Entity<Jedi> entity = Entity.identify(new Jedi("Obiwan", "Jedi"))
 *         .withId("42")
 *         .meta("createdBy", "Lucas");
 *
 * Map<String, Object> map = mapper.toMap(entity);
 *
 * // Resulting Map (serialized structure depends on EntitySerializer):
 * // {
 * //   "_id": "42",
 * //   "_createdBy": "Lucas",
 * //   "name": "Obiwan",
 * //   "faction": "Jedi"
 * // }
 * }</pre>
 *
 * @see ObjectMapper#convertValue(Object, TypeReference)
 */
@RequiredArgsConstructor
public class Jackson3EntityMapper implements EntityMapper {
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    private final ObjectMapper mapper;

    @Override
    public String entityIdentifierProperty() {
        return EntityModuleConstant.IDENTIFIER;
    }

    @Override
    public Map<String, Object> toMap(Entity<?> entity) {
        return mapper.convertValue(entity, MAP_TYPE_REFERENCE);
    }
}

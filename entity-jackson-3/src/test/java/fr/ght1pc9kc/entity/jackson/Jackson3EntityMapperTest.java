package fr.ght1pc9kc.entity.jackson;

import fr.ght1pc9kc.entity.api.Entity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

class Jackson3EntityMapperTest {
    private Jackson3EntityMapper tested;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder()
                .addModule(new EntityModule())
                .build();
        tested = new Jackson3EntityMapper(jsonMapper);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("fr.ght1pc9kc.entity.jackson.samples.Samples#providerForDeserializedSerialized")
    void should_convert_value_to_map(Object obj, Object ignore, String expectedJson) {
        if (obj instanceof Entity<?> entity) {
            Assertions.assertThat(tested.toMap(entity))
                    .isEqualTo(jsonMapper.readValue(expectedJson, new TypeReference<Map<String, Object>>() {
                    }));

        }
    }
}
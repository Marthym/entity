package fr.ght1pc9kc.entity.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.ght1pc9kc.entity.api.Entity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;

class Jackson2EntityMapperTest {
    private Jackson2EntityMapper tested;
    private ObjectMapper jsonMapper;

    @BeforeEach
    void setUp() {
        jsonMapper = new ObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new EntityModule());
        tested = new Jackson2EntityMapper(jsonMapper);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("fr.ght1pc9kc.entity.jackson.samples.Samples#providerForDeserializedSerialized")
    void should_convert_value_to_map(Object obj, Object ignore, String expectedJson) throws JsonProcessingException {
        if (obj instanceof Entity<?> entity) {
            Assertions.assertThat(tested.toMap(entity))
                    .isEqualTo(jsonMapper.readValue(expectedJson, new TypeReference<Map<String, Object>>() {
                    }));

        }
    }
}
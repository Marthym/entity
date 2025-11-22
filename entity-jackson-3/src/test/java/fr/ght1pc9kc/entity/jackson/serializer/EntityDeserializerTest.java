package fr.ght1pc9kc.entity.jackson.serializer;

import fr.ght1pc9kc.entity.jackson.EntityModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

class EntityDeserializerTest {
    private ObjectMapper tested;

    @BeforeEach
    void setUp() {
        this.tested = JsonMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addModule(new EntityModule())
                .build();
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("fr.ght1pc9kc.entity.jackson.samples.Samples#providerForDeserializedSerialized")
    void should_deserialize_entity(Object value, TypeReference<?> type, String json) {
        Object actual = this.tested.readValue(json, type);

        Class<?> expectedClass = tested.getTypeFactory().constructType(type).getRawClass();
        Assertions.assertThat(actual).isNotNull()
                .isInstanceOf(expectedClass)
                .isEqualTo(value);
    }

}
package fr.ght1pc9kc.entity.jackson.serializer;

import fr.ght1pc9kc.entity.jackson.EntityModule;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

class EntitySerializerTest {
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
    void should_serialize_polymorphic_entity(Object entity, Object ignore, String expectedJson) {
        String actual = this.tested.writerWithDefaultPrettyPrinter()
                .writeValueAsString(entity);

        JsonAssertions.assertThatJson(actual).isEqualTo(expectedJson);
    }
}
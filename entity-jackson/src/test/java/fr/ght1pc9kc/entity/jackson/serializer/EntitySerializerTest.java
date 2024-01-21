package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.ght1pc9kc.entity.jackson.EntityModule;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EntitySerializerTest {
    private ObjectMapper tested;

    @BeforeEach
    void setUp() {
        this.tested = new ObjectMapper();
        this.tested.registerModule(new JavaTimeModule())
                .registerModule(new EntityModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("fr.ght1pc9kc.entity.jackson.samples.Samples#providerForDeserializedSerialized")
    void should_serialize_polymorphic_entity(Object entity, Object ignore, String expectedJson) throws JsonProcessingException {
        String actual = this.tested.writerWithDefaultPrettyPrinter()
                .writeValueAsString(entity);

        JsonAssertions.assertThatJson(actual).isEqualTo(expectedJson);
    }
}
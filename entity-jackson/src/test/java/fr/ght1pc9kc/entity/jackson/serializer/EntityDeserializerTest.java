package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.ght1pc9kc.entity.jackson.EntityModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EntityDeserializerTest {
    private ObjectMapper tested;

    @BeforeEach
    void setUp() {
        this.tested = new ObjectMapper();
        this.tested.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .registerModule(new EntityModule());
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("fr.ght1pc9kc.entity.jackson.samples.Samples#providerForDeserializedSerialized")
    void should_deserialize_entity(Object value, TypeReference<?> type, String json) throws JsonProcessingException {
        Object actual = this.tested.readValue(json, type);

        Class<?> expectedClass = tested.getTypeFactory().constructType(type).getRawClass();
        Assertions.assertThat(actual).isNotNull()
                .isInstanceOf(expectedClass)
                .isEqualTo(value);
    }

}
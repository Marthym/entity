package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.ght1pc9kc.entity.jackson.EntityModule;
import fr.ght1pc9kc.entity.jackson.Samples;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntitySerializerTest {
    private ObjectMapper tested;

    @BeforeEach
    void setUp() {
        this.tested = new ObjectMapper();
        this.tested.registerModule(new JavaTimeModule())
                .registerModule(new EntityModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void should_serialize_simple_object() throws JsonProcessingException {
        String actual = this.tested.writerWithDefaultPrettyPrinter()
                .writeValueAsString(Samples.SIMPLE_WRAPPED);

        JsonAssertions.assertThatJson(actual).isEqualTo("""
                {
                    "_id": "ID42",
                    "_createdAt": "2024-01-20T15:06:42.546Z",
                    "_createdBy": "okenobi",
                    "message": "May the \\"force\\""
                }
                """);
    }
}
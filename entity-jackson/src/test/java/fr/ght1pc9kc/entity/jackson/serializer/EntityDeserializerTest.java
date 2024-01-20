package fr.ght1pc9kc.entity.jackson.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.jackson.EntityModule;
import fr.ght1pc9kc.entity.jackson.Samples;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntityDeserializerTest {
    private ObjectMapper tested;

    @BeforeEach
    void setUp() {
        this.tested = new ObjectMapper();
        this.tested.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .registerModule(new EntityModule());
    }

    @Test
    void should_deserialize_entity() throws JsonProcessingException {
        Entity<Samples.Simple> actual = this.tested.readValue("""
                {
                    "_id": "ID42",
                    "_createdAt": "2024-01-20T15:06:42.546Z",
                    "_createdBy": "okenobi",
                    "message": "May the \\"force\\""
                }
                """, new TypeReference<>() {
        });

        Assertions.assertThat(actual).isNotNull()
                .isEqualTo(Samples.SIMPLE_WRAPPED);
    }
}
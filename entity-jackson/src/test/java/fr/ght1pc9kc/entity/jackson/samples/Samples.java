package fr.ght1pc9kc.entity.jackson.samples;


import com.fasterxml.jackson.core.type.TypeReference;
import fr.ght1pc9kc.entity.api.Entity;
import org.junit.jupiter.params.provider.Arguments;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public abstract class Samples {
    public static final Entity<String> STRING_ENTITY = Entity
            .identify("May the \"force\"")
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .withId("ID42");

    public static final Entity<List<String>> LIST_ENTITY = Entity
            .identify(List.of("May the \"force\""))
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .withId("ID42");

    public static final Entity<Simple> SIMPLE_WRAPPED = Entity
            .identify(new Simple("May the \"force\""))
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .withId("ID42");

    public static final Entity<Saber> POLYMORPHIC_ENTITY_1 = Entity
            .<Saber>identify(new DarkSaber(null))
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("TarreVizsla")
            .withId("DARKSABER");

    public static final Entity<Saber> POLYMORPHIC_ENTITY_2 = Entity
            .<Saber>identify(new LightSaber(Color.GREEN, 1))
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .withId("LIGHTSABER");

    public static Stream<Arguments> providerForDeserializedSerialized() {
        return Stream.of(
                Arguments.of(Samples.LIST_ENTITY, new TypeReference<Entity<List<String>>>() {
                }, """
                        {
                            "_id": "ID42",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "okenobi",
                            "_self": ["May the \\"force\\""]
                        }
                        """),
                Arguments.of(Samples.STRING_ENTITY, new TypeReference<Entity<String>>() {
                }, """
                        {
                            "_id": "ID42",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "okenobi",
                            "_self": "May the \\"force\\""
                        }
                        """),
                Arguments.of(Samples.SIMPLE_WRAPPED, new TypeReference<Entity<Simple>>() {
                }, """
                        {
                            "_id": "ID42",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "okenobi",
                            "message": "May the \\"force\\""
                        }
                        """),
                Arguments.of(Samples.POLYMORPHIC_ENTITY_1, new TypeReference<Entity<Saber>>() {
                }, """
                        {
                            "_id": "DARKSABER",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "TarreVizsla",
                            "@type": "DARK",
                            "color": "SHADOW"
                        }
                        """),
                Arguments.of(Samples.POLYMORPHIC_ENTITY_2, new TypeReference<Entity<Saber>>() {
                }, """
                        {
                            "_id": "LIGHTSABER",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "okenobi",
                            "@type": "LIGHT",
                            "blade": 1,
                            "color": "GREEN"
                        }
                        """),
                Arguments.of(List.of(Samples.POLYMORPHIC_ENTITY_1, Samples.POLYMORPHIC_ENTITY_2),
                        new TypeReference<List<Entity<Saber>>>() {
                        }, """
                                [{
                                    "_id": "DARKSABER",
                                    "_createdAt": "2024-01-20T15:06:42.546Z",
                                    "_createdBy": "TarreVizsla",
                                    "@type": "DARK",
                                    "color": "SHADOW"
                                },{
                                    "_id": "LIGHTSABER",
                                    "_createdAt": "2024-01-20T15:06:42.546Z",
                                    "_createdBy": "okenobi",
                                    "@type": "LIGHT",
                                    "blade": 1,
                                    "color": "GREEN"
                                }]
                                """)
        );
    }
}

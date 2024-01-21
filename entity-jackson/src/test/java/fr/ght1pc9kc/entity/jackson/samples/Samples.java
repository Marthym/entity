package fr.ght1pc9kc.entity.jackson.samples;


import com.fasterxml.jackson.core.type.TypeReference;
import fr.ght1pc9kc.entity.api.Entity;
import org.junit.jupiter.params.provider.Arguments;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public abstract class Samples {
    public static final Entity<Simple> SIMPLE_WRAPPED = Entity.<Simple>builder()
            .id("ID42")
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .self(new Simple("May the \"force\""))
            .build();

    public static final Entity<Saber> POLYMORPHIC_ENTITY_1 = Entity.<Saber>builder()
            .id("DARKSABER")
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("TarreVizsla")
            .self(new DarkSaber(null))
            .build();

    public static final Entity<Saber> POLYMORPHIC_ENTITY_2 = Entity.<Saber>builder()
            .id("LIGHTSABER")
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .self(new LightSaber(Color.GREEN, 1))
            .build();

    public static Stream<Arguments> providerForDeserializedSerialized() {
        return Stream.of(
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

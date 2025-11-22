package fr.ght1pc9kc.entity.jackson.samples;


import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import org.junit.jupiter.params.provider.Arguments;
import tools.jackson.core.type.TypeReference;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static fr.ght1pc9kc.entity.jackson.samples.DefaultMetaProperties.createdAt;
import static fr.ght1pc9kc.entity.jackson.samples.DefaultMetaProperties.createdBy;

@SuppressWarnings("unused")
public abstract class Samples {
    public static final Entity<String> STRING_ENTITY = Entity
            .identify("May the \"force\"")
            .withId("ID42");

    public static final Entity<List<String>> LIST_ENTITY = Entity
            .identify(List.of("May the \"force\""))
            .meta(createdAt, Instant.parse("2024-01-20T15:06:42.546Z"))
            .meta(createdBy, "okenobi")
            .withId("ID42");

    public static final Entity<Simple> SIMPLE_WRAPPED = Entity
            .identify(new Simple("May the \"force\""))
            .meta(createdAt, Instant.parse("2024-01-20T15:06:42.546Z"))
            .meta(createdBy, "okenobi")
            .withId("ID42");

    public static final Entity<Saber> POLYMORPHIC_ENTITY_1 = Entity
            .<Saber>identify(new DarkSaber(null))
            .meta(createdAt, Instant.parse("2024-01-20T15:06:42.546Z"))
            .meta(createdBy, "TarreVizsla")
            .withId("DARKSABER");

    public static final Entity<Saber> POLYMORPHIC_ENTITY_2 = Entity
            .<Saber>identify(new LightSaber(Color.GREEN, 1))
            .meta(createdAt, Instant.parse("2024-01-20T15:06:42.546Z"))
            .meta(createdBy, "okenobi")
            .withId("LIGHTSABER");

    public static Stream<Arguments> providerForDeserializedSerialized() {
        return Stream.of(
                Arguments.of(Samples.LIST_ENTITY, new TypeReference<ExtendedEntity<List<String>, DefaultMetaProperties>>() {
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
                            "_self": "May the \\"force\\""
                        }
                        """),
                Arguments.of(Samples.SIMPLE_WRAPPED, new TypeReference<ExtendedEntity<Simple, DefaultMetaProperties>>() {
                }, """
                        {
                            "_id": "ID42",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "okenobi",
                            "message": "May the \\"force\\""
                        }
                        """),
                Arguments.of(Samples.POLYMORPHIC_ENTITY_1, new TypeReference<ExtendedEntity<Saber, DefaultMetaProperties>>() {
                }, """
                        {
                            "_id": "DARKSABER",
                            "_createdAt": "2024-01-20T15:06:42.546Z",
                            "_createdBy": "TarreVizsla",
                            "@type": "DARK",
                            "color": "SHADOW"
                        }
                        """),
                Arguments.of(Samples.POLYMORPHIC_ENTITY_2, new TypeReference<ExtendedEntity<Saber, DefaultMetaProperties>>() {
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
                        new TypeReference<List<ExtendedEntity<Saber, DefaultMetaProperties>>>() {
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

package fr.ght1pc9kc.entity.graphql;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.TypedMeta;
import graphql.GraphQLContext;
import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EntityDataFetcherTest {
    private EntityDataFetcher tested;

    @BeforeEach
    void setUp() {
        tested = EntityDataFetcher.builder().build();
    }

    @ParameterizedTest(name = "[{index}] field with expected value {1}")
    @MethodSource("dataFetchingEnvironments")
    void should_fetch_data(String property, GraphQLContext context, Object expected) {

        Entity<Dummy> source = Entity.identify(new Dummy("Obiwan", "Jedi", "Lightsaber", "Tatooine"))
                .meta(DummyMetas.createdBy, "Qui-Gon Jin")
                .withId("42");

        if (property == null) {
            assertThat(tested.get(DataFetchingEnvironmentImpl.newDataFetchingEnvironment().build())).isNull();
        } else {
            DataFetchingEnvironment dfe = DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                    .mergedField(MergedField.newMergedField(Field.newField(property).build()).build())
                    .graphQLContext(context)
                    .source(source)
                    .build();

            assertThat(tested.get(dfe)).isEqualTo(expected);
        }
    }

    private static Stream<Arguments> dataFetchingEnvironments() {

        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of("_id", GraphQLContext.newContext().build(), "42"),
                Arguments.of("weapon", GraphQLContext.newContext().build(), "Lightsaber"),
                Arguments.of("planet", GraphQLContext.newContext().build(), null),
                Arguments.of("_createdBy", GraphQLContext.newContext().build(), "Qui-Gon Jin"),
                Arguments.of("name", GraphQLContext.newContext().build(), "Obiwan"),
                Arguments.of("faction", GraphQLContext.newContext().of(
                        "entityAsMap", Map.of("_id", "42", "faction", "Sith")).build(), "Sith"),
                Arguments.of("faction", GraphQLContext.newContext().of(
                        "entityAsMap", Map.of("_id", "43", "faction", "Sith")).build(), "Jedi")
        );
    }

    @SuppressWarnings("java:S115")
    private enum DummyMetas implements TypedMeta {
        createdBy(String.class);

        private final Class<?> type;

        DummyMetas(Class<?> type) {
            this.type = type;
        }

        public Class<?> type() {
            return this.type;
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static final class Dummy {
        public final String name;
        public final String faction;
        @Getter
        private final String weapon;
        private final String planet;

        public Dummy(String name, String faction, String weapon, String planet) {
            this.name = name;
            this.faction = faction;
            this.weapon = weapon;
            this.planet = planet;
        }
    }
}
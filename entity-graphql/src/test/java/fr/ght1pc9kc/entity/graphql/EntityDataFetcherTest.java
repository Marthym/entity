package fr.ght1pc9kc.entity.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.TypedMeta;
import fr.ght1pc9kc.entity.jackson.EntityModule;
import graphql.GraphQLContext;
import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentImpl;
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
        ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules()
                .registerModule(new EntityModule());
        tested = new EntityDataFetcher(mapper);
    }

    @ParameterizedTest
    @MethodSource("dataFetchingEnvironments")
    void should_fetch_data(DataFetchingEnvironment dfe, Object expected) {
        assertThat(tested.get(dfe)).isEqualTo(expected);
    }

    private static Stream<Arguments> dataFetchingEnvironments() {
        Entity<Dummy> source = Entity.identify(new Dummy("Obiwan", "Jedi"))
                .meta(DummyMetas.createdBy, "Qui-Gon Jin")
                .withId("42");

        return Stream.of(
                Arguments.of(DataFetchingEnvironmentImpl.newDataFetchingEnvironment().build(), null),
                Arguments.of(DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                        .mergedField(MergedField.newMergedField(Field.newField("_id").build()).build())
                        .graphQLContext(GraphQLContext.newContext().build())
                        .source(source)
                        .build(), "42"),
                Arguments.of(DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                        .mergedField(MergedField.newMergedField(Field.newField("_createdBy").build()).build())
                        .graphQLContext(GraphQLContext.newContext().build())
                        .source(source)
                        .build(), "Qui-Gon Jin"),
                Arguments.of(DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                        .mergedField(MergedField.newMergedField(Field.newField("name").build()).build())
                        .graphQLContext(GraphQLContext.newContext().build())
                        .source(source)
                        .build(), "Obiwan"),
                Arguments.of(DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                        .mergedField(MergedField.newMergedField(Field.newField("faction").build()).build())
                        .graphQLContext(GraphQLContext.newContext().of(
                                "entityAsMap", Map.of("_id", "42", "faction", "Sith")
                        ).build())
                        .source(source)
                        .build(), "Sith"),
                Arguments.of(DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                        .mergedField(MergedField.newMergedField(Field.newField("faction").build()).build())
                        .graphQLContext(GraphQLContext.newContext().of(
                                "entityAsMap", Map.of("_id", "43", "faction", "Sith")
                        ).build())
                        .source(source)
                        .build(), "Jedi")
        );
    }

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

    private static final class Dummy {
        public final String name;
        public final String faction;

        public Dummy(String name, String faction) {
            this.name = name;
            this.faction = faction;
        }
    }
}
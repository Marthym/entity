package fr.ght1pc9kc.entity.graphql;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.TypedMeta;
import graphql.TypeResolutionEnvironment;
import graphql.execution.TypeResolutionParameters;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTypeResolverTest {

    private final EntityTypeResolver tested = new EntityTypeResolver();

    @Test
    void should_resolve_type() {
        GraphQLObjectType dummyType = GraphQLObjectType.newObject()
                .name("Dummy")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("name")
                        .type(GraphQLTypeReference.typeRef("String"))
                        .build())
                .build();
        TypeResolutionEnvironment typeResolutionEnvironment = TypeResolutionParameters.newParameters()
                .value(Entity.identify(new Dummy("Obiwan", "Jedi"))
                        .meta(DummyMetas.createdBy, "Qui-Gon Jin")
                        .withId("42"))
                .schema(GraphQLSchema.newSchema()
                        .additionalType(dummyType)
                        .query(GraphQLObjectType.newObject().name("test").field(GraphQLFieldDefinition.newFieldDefinition()
                                .name("name")
                                .type(GraphQLTypeReference.typeRef("String"))
                                .build()).build())
                        .additionalDirective(GraphQLDirective.newDirective().name("directive_test").build())
                        .build())
                .build();

        assertThat(tested.getType(typeResolutionEnvironment)).isEqualTo(dummyType);
    }

    @Test
    void should_throw_exception_on_non_Entity() {
        GraphQLObjectType dummyType = GraphQLObjectType.newObject()
                .name("Dummy")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("name")
                        .type(GraphQLTypeReference.typeRef("String"))
                        .build())
                .build();
        TypeResolutionEnvironment typeResolutionEnvironment = TypeResolutionParameters.newParameters()
                .value(new Dummy("Obiwan", "Jedi"))
                .schema(GraphQLSchema.newSchema()
                        .additionalType(dummyType)
                        .query(GraphQLObjectType.newObject().name("test").field(GraphQLFieldDefinition.newFieldDefinition()
                                .name("name")
                                .type(GraphQLTypeReference.typeRef("String"))
                                .build()).build())
                        .additionalDirective(GraphQLDirective.newDirective().name("directive_test").build())
                        .build())
                .build();

        Assertions.assertThatThrownBy(() -> tested.getType(typeResolutionEnvironment))
                .isInstanceOf(IllegalStateException.class);
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
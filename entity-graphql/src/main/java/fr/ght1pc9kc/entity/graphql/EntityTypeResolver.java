package fr.ght1pc9kc.entity.graphql;

import fr.ght1pc9kc.entity.api.Entity;
import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class EntityTypeResolver implements TypeResolver {
    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
        Object object = env.getObject();
        if (object instanceof Entity<?> entity) {
            return env.getSchema().getObjectType(entity.self().getClass().getSimpleName());
        }
        throw new IllegalStateException("Expecting Entity for GraphQL type " + env.getField());
    }
}


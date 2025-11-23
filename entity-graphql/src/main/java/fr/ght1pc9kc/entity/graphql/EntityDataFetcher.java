package fr.ght1pc9kc.entity.graphql;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.mapper.EntityMapper;
import fr.ght1pc9kc.entity.graphql.mapper.DefaultEntityMapper;
import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcherHelper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityDataFetcher implements DataFetcher<Object> {
    private static final String ENTITY_AS_MAP_KEY = "entityAsMap";
    @Builder.Default
    private final EntityMapper mapper = new DefaultEntityMapper();

    @Override
    public Object get(DataFetchingEnvironment env) {
        if (env.getSource() == null) {
            return null;
        } else if (env.getSource() instanceof Entity<?> source) {
            GraphQLContext localContext = env.getGraphQlContext();
            Optional<Map<String, Object>> jacksonConvertedEntity = localContext.getOrEmpty(ENTITY_AS_MAP_KEY);
            if (jacksonConvertedEntity.isEmpty() || !jacksonConvertedEntity.get().get(mapper.entityIdentifierProperty()).equals(source.id())) {
                Map<String, Object> entityAsMap = mapper.toMap(source);
                localContext.put(ENTITY_AS_MAP_KEY, entityAsMap);
                return entityAsMap.get(env.getField().getName());
            }
            return jacksonConvertedEntity.get().get(env.getField().getName());
        } else {
            return PropertyDataFetcherHelper.getPropertyValue(env.getField().getName(), env.getSource(), env.getFieldType(), () -> env);
        }
    }
}

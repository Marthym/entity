package fr.ght1pc9kc.entity.graphql;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ght1pc9kc.entity.api.Entity;
import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class EntityDataFetcher implements DataFetcher<Object> {
    private static final String ENTITY_AS_MAP_KEY = "entityAsMap";
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    private final ObjectMapper mapper;

    @Override
    public Object get(DataFetchingEnvironment env) {
        GraphQLContext localContext = env.getGraphQlContext();
        Optional<Map<String, Object>> jacksonConvertedEntity = localContext.getOrEmpty(ENTITY_AS_MAP_KEY);
        if (jacksonConvertedEntity.isEmpty()) {
            Entity<?> source = env.getSource();
            Map<String, Object> entityAsMap = mapper.convertValue(source, MAP_TYPE_REFERENCE);
            localContext.put(ENTITY_AS_MAP_KEY, entityAsMap);
            return entityAsMap.get(env.getField().getName());
        }
        return jacksonConvertedEntity.get().get(env.getField().getName());
    }
}

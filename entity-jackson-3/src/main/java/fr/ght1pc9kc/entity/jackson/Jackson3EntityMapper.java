package fr.ght1pc9kc.entity.jackson;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RequiredArgsConstructor
public class Jackson3EntityMapper implements EntityMapper {
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    private final ObjectMapper mapper;

    @Override
    public String entityIdentifierProperty() {
        return EntityModuleConstant.IDENTIFIER;
    }

    @Override
    public Map<String, Object> toMap(Entity<?> entity) {
        return mapper.convertValue(entity, MAP_TYPE_REFERENCE);
    }
}

package fr.ght1pc9kc.entity.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class Jackson2EntityMapper implements EntityMapper {
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

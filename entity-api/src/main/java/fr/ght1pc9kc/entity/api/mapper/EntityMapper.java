package fr.ght1pc9kc.entity.api.mapper;

import fr.ght1pc9kc.entity.api.Entity;

import java.util.Map;

public interface EntityMapper {
    String entityIdentifierProperty();

    Map<String, Object> toMap(Entity<?> entity);

    default void errorHandler(Throwable ignoredThrow, Class<?> ignoredClazz) {
        // No error handling by default
    }
}

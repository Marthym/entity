package fr.ght1pc9kc.entity.api.impl;

import fr.ght1pc9kc.entity.api.Entity;
import lombok.NonNull;

import java.util.Optional;

/**
 * Hold the standard Persistence information
 *
 * @param self The Persisted Entity
 * @param <T>  The type of the persisted object
 */
public record BasicEntity<T>(@NonNull String id,
                             @NonNull T self) implements Entity<T> {
    @Override
    public <M> Optional<M> meta(Enum<?> property, Class<M> type) {
        return Optional.empty();
    }
}

package fr.ght1pc9kc.entity.api.impl;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.builders.BasicEntityBuilder;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;

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

    @Override
    public <S> Entity<S> convert(Function<T, S> converter) {
        return new BasicEntity<>(id(), converter.apply(self()));
    }

    @Override
    public <S extends Enum<S>> Entity<T> withMeta(S key, Object value) {
        return new BasicEntityBuilder<>(self()).meta(key, value).withId(id());
    }
}

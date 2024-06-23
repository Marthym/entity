package fr.ght1pc9kc.entity.api;

import fr.ght1pc9kc.entity.api.builders.BasicEntityBuilder;
import fr.ght1pc9kc.entity.api.impl.BasicEntity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Entity interface. This is recommended to use it.
 *
 * @param <T>
 */
public sealed interface Entity<T> permits BasicEntity, ExtendedEntity {
    String id();

    <M> Optional<M> meta(Enum<?> property, Class<M> type);

    default Optional<String> meta(Enum<?> property) {
        return meta(property, String.class);
    }

    T self();

    <S> Entity<S> convert(Function<T, S> converter);

    <S extends Enum<S>> Entity<T> withMeta(S key, Object value);

    /**
     * Give the builder to create entity
     *
     * @param entity The self-contained entity
     * @param <T>    The type of the self-contained Entity object
     * @return The Entity, identified with ID
     */
    static <T> BasicEntityBuilder<T> identify(@NotNull T entity) {
        return new BasicEntityBuilder<>(Objects.requireNonNull(entity, "Self entity must not be null !"));
    }
}

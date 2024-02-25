package fr.ght1pc9kc.entity.api;

import fr.ght1pc9kc.entity.api.builders.BasicEntityBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public interface Entity<T> {
    String id();

    <M> Optional<M> meta(Enum<?> property, Class<M> type);

    default Optional<String> meta(Enum<?> property) {
        return meta(property, String.class);
    }

    T self();

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

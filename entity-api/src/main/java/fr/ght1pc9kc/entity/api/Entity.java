package fr.ght1pc9kc.entity.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

/**
 * Hold the standard Persistence information
 *
 * @param self The Persisted Entity
 * @param <T>  The type of the persisted object
 */
public record Entity<T>(
        @NonNull String id,
        @NonNull String createdBy,
        @NonNull Instant createdAt,
        @NonNull T self
) {
    /**
     * Main entity identifier
     */
    public static final String IDENTIFIER = "_id";
    /**
     * The instant the entity was created
     */
    public static final String CREATED_AT = "_createdAt";
    /**
     * The identifier of entity which create the entity
     */
    public static final String CREATED_BY = "_createdBy";
    /**
     * The serialized property name for self-contained value if not an object
     */
    public static final String SELF = "_self";
    /**
     * The unknown creator defautl value
     */
    public static final String NO_ONE = "_";

    /**
     * Give the builder to create entity
     *
     * @param entity The self-contained entity
     * @param <T>    The type of the self-contained Entity object
     * @return The Entity, identified with ID
     */
    public static <T> EntityBuilder<T> identify(@NotNull T entity) {
        Objects.requireNonNull(entity, "Self entity must not be null !");
        return new EntityBuilder<>(entity);
    }

    /**
     * The {@link Entity} builder
     *
     * @param <T> A well-formed Entity
     */
    @RequiredArgsConstructor
    public static final class EntityBuilder<T> {
        private final T self;
        private Instant createdAt;
        private String createdBy;

        /**
         * Add optional creation time to the entity. Default was {@link Instant#EPOCH}
         *
         * @param createdAt Entity creation time
         * @return The builder
         */
        public EntityBuilder<T> createdAt(@Nullable Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * Add optional creator identifier to the entity. Default was {@link Entity#NO_ONE}
         *
         * @param createdBy Entity creator identifier
         * @return The builder
         */
        public EntityBuilder<T> createdBy(@Nullable String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        /**
         * Give the identifer to the Entity and build it as an immutable object
         *
         * @param id Identifier of the entity
         * @return The Entity
         */
        public Entity<T> withId(@NotNull String id) {
            Objects.requireNonNull(id, "ID is mandatory for Entity !");
            return new Entity<>(id,
                    (createdBy == null) ? NO_ONE : createdBy,
                    (createdAt == null) ? Instant.EPOCH : createdAt,
                    self);
        }
    }
}

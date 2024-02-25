package fr.ght1pc9kc.entity.api.builders;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.impl.BasicEntity;
import fr.ght1pc9kc.entity.api.impl.ExtendedEntity;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Objects;

/**
 * Build an {@link ExtendedEntity} containing the entity itself, the ID and some meta information about the entity.
 *
 * @param <T> A well-formed Entity
 * @param <E> The Enum representing all the possible meta-property of the entity
 */
public final class ExtendedEntityBuilder<T, E extends Enum<E>> {
    private final T self;
    private final EnumMap<E, Object> metas;

    public ExtendedEntityBuilder(T self, Class<E> metasType) {
        this.self = self;
        this.metas = new EnumMap<>(metasType);
    }

    /**
     * Add meta-information to the {@link Entity}
     *
     * @param property An Enum value
     * @param value    The value of property
     * @return The {@link ExtendedEntityBuilder}
     */
    public ExtendedEntityBuilder<T, E> meta(E property, Object value) {
        this.metas.put(property, value);
        return this;
    }

    /**
     * Give the identifier to the Entity and build it as an immutable object
     *
     * @param id Identifier of the entity
     * @return The Entity
     */
    public Entity<T> withId(@NotNull String id) {
        Objects.requireNonNull(id, "ID is mandatory for Entity !");
        if (this.metas.isEmpty()) {
            return new BasicEntity<>(id, this.self);
        }
        return new ExtendedEntity<>(id, new EnumMap<>(this.metas), this.self);
    }
}

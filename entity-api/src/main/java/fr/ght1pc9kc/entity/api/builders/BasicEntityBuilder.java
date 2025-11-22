package fr.ght1pc9kc.entity.api.builders;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.TypedMeta;
import fr.ght1pc9kc.entity.api.impl.BasicEntity;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * The {@link Entity} builder
 *
 * @param <T> A well-formed Entity
 */
public final class BasicEntityBuilder<T> {
    private final T self;

    public BasicEntityBuilder(T self) {
        this.self = self;
    }

    public <E extends Enum<E>> ExtendedEntityBuilder<T, E> meta(E property, Object value) {
        if (value == null) {
            return new ExtendedEntityBuilder<>(self, property.getDeclaringClass());
        }
        if (property instanceof TypedMeta typed && !typed.type().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Value type "
                    + value.getClass() + "incompatible with "
                    + typed.type() + " from " + property.getClass());
        }
        return new ExtendedEntityBuilder<>(self, property.getDeclaringClass())
                .meta(property, value);
    }

    /**
     * Give the identifier to the Entity and build it as an immutable object
     *
     * @param id Identifier of the entity
     * @return The Entity
     */
    public Entity<T> withId(@NonNull String id) {
        Objects.requireNonNull(id, "ID is mandatory for Entity !");
        return new BasicEntity<>(id, this.self);
    }
}

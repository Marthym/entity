package fr.ght1pc9kc.entity.api.impl;

import fr.ght1pc9kc.entity.api.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Optional;

public record ExtendedEntity<T, E extends Enum<E>>(
        @NotNull String id,
        @NotNull EnumMap<E, Object> metas,
        @NotNull T self
) implements Entity<T> {
    public ExtendedEntity {
        metas = new EnumMap<>(metas);
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public <M> Optional<M> meta(Enum<?> property, Class<M> type) {
        return Optional.ofNullable(metas.get(property))
                .map(type::cast);
    }

    @Override
    public EnumMap<E, Object> metas() {
        return new EnumMap<>(metas);
    }
}

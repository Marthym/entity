package fr.ght1pc9kc.entity.api.impl;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.api.builders.ExtendedEntityBuilder;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

public record ExtendedEntity<T, E extends Enum<E>>(
        @NonNull String id,
        @NonNull EnumMap<E, Object> metas,
        @NonNull T self
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
    public <S> Entity<S> convert(Function<T, S> converter) {
        return new ExtendedEntity<>(id(), metas(), converter.apply(self()));
    }

    @Override
    public <S extends Enum<S>> Entity<T> withMeta(S key, Object value) {
        E validKey = Enum.valueOf(metas.keySet().iterator().next().getDeclaringClass(), key.name());
        ExtendedEntityBuilder<T, E> builder = new ExtendedEntityBuilder<>(self(), validKey.getDeclaringClass());
        this.metas.forEach(builder::meta);
        builder.meta(validKey, value);
        return builder.withId(id());
    }

    @Override
    public EnumMap<E, Object> metas() {
        return new EnumMap<>(metas);
    }

}

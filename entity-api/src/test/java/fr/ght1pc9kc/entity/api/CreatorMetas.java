package fr.ght1pc9kc.entity.api;

import java.time.Instant;

public enum CreatorMetas implements TypedMeta {
    createdAt(Instant.class), createdBy(String.class), dummy(Void.class);

    private final Class<?> type;

    CreatorMetas(Class<?> type) {
        this.type = type;
    }

    public Class<?> type() {
        return this.type;
    }
}

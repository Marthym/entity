package fr.ght1pc9kc.entity.jackson.samples;

import fr.ght1pc9kc.entity.jackson.TypedMeta;

import java.time.Instant;

public enum DefaultMetaProperties implements TypedMeta {
    createdAt(Instant.class), createdBy(String.class);

    private final Class<?> type;

    DefaultMetaProperties(Class<?> type) {
        this.type = type;
    }

    public Class<?> type() {
        return type;
    }
}

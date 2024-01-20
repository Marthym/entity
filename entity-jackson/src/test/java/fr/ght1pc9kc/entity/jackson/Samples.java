package fr.ght1pc9kc.entity.jackson;


import fr.ght1pc9kc.entity.api.Entity;

import java.time.Instant;

public abstract class Samples {
    public static final Entity<Simple> SIMPLE_WRAPPED = Entity.<Simple>builder()
            .id("ID42")
            .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
            .createdBy("okenobi")
            .self(new Simple("May the \"force\""))
            .build();

    public record Simple(String message) {
    }
}

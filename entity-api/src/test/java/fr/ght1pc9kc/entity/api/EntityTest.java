package fr.ght1pc9kc.entity.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;

class EntityTest {
    @Test
    void should_create_default_entity() {
        Entity<String> actual = Entity.identify("May the force").withId("4TH");
        assertAll(
                () -> Assertions.assertThat(actual.id()).isEqualTo("4TH"),
                () -> Assertions.assertThat(actual.createdAt()).isEqualTo(Instant.EPOCH),
                () -> Assertions.assertThat(actual.createdBy()).isEqualTo(Entity.NO_ONE),
                () -> Assertions.assertThat(actual.self()).isEqualTo("May the force")
        );
    }

    @Test
    void should_create_complete_entity() {
        Instant createdAt = Instant.parse("2024-01-21T15:41:42.660Z");
        Entity<String> actual = Entity.identify("May the force")
                .createdBy("Yoda")
                .createdAt(createdAt)
                .withId("4TH");
        assertAll(
                () -> Assertions.assertThat(actual.id()).isEqualTo("4TH"),
                () -> Assertions.assertThat(actual.createdAt()).isEqualTo(createdAt),
                () -> Assertions.assertThat(actual.createdBy()).isEqualTo("Yoda"),
                () -> Assertions.assertThat(actual.self()).isEqualTo("May the force")
        );
    }
}
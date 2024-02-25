package fr.ght1pc9kc.entity.api;

import fr.ght1pc9kc.entity.api.builders.BasicEntityBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static fr.ght1pc9kc.entity.api.CreatorMetas.createdAt;
import static fr.ght1pc9kc.entity.api.CreatorMetas.createdBy;
import static fr.ght1pc9kc.entity.api.CreatorMetas.dummy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityTest {
    @Test
    void should_create_default_entity() {
        Entity<String> actual = Entity.identify("May the force").withId("4TH");
        assertAll(
                () -> Assertions.assertThat(actual.id()).isEqualTo("4TH"),
                () -> Assertions.assertThat(actual.self()).isEqualTo("May the force")
        );
    }

    @Test
    void should_create_complete_entity() {
        Instant createdDate = Instant.parse("2024-01-21T15:41:42.660Z");
        Entity<String> actual = Entity.identify("May the force")
                .meta(createdBy, "Yoda")
                .meta(createdAt, createdDate)
                .withId("4TH");
        assertAll(
                () -> Assertions.assertThat(actual.id()).isEqualTo("4TH"),
                () -> Assertions.assertThat(actual.meta(createdAt, Instant.class))
                        .isPresent().contains(createdDate),
                () -> Assertions.assertThat(actual.meta(createdBy))
                        .isPresent().contains("Yoda"),
                () -> Assertions.assertThat(actual.meta(dummy)).isEmpty(),
                () -> Assertions.assertThat(actual.meta(DummyProps.obiwan)).isEmpty(),
                () -> Assertions.assertThat(actual.self()).isEqualTo("May the force")
        );
    }

    @Test
    void should_fail_on_wrong_type() {
        BasicEntityBuilder<String> actual = Entity.identify("May the force");
        Assertions.assertThatThrownBy(() -> actual.meta(createdAt, "Yoda"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public enum DummyProps {
        obiwan, kenobi
    }
}
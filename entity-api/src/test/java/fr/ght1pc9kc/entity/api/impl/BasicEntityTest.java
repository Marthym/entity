package fr.ght1pc9kc.entity.api.impl;

import fr.ght1pc9kc.entity.api.Entity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BasicEntityTest {
    @Test
    void should_get_meta_from_basic_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .withId("42");

        Assertions.assertThat(tested.meta(DummyMeta.FACTION)).isEmpty();
    }

    @Test
    void should_convert_basic_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .withId("42");
        var actual = tested.convert(l -> new Dummy(l.name() + " kenobi"));

        Assertions.assertThat(actual.self().name()).isEqualTo("obiwan kenobi");
    }

    @Test
    void should_add_meta_to_basic_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .withId("42");
        var actual = tested.withMeta(DummyMeta.FACTION, "jedi");
        Assertions.assertThat(actual.meta(DummyMeta.FACTION)).contains("jedi");
    }

    public record Dummy(String name) {
    }

    public enum DummyMeta {FACTION, MASTER;}
}
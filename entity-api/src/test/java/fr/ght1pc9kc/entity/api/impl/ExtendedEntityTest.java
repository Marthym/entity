package fr.ght1pc9kc.entity.api.impl;

import fr.ght1pc9kc.entity.api.Entity;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class ExtendedEntityTest {
    @Test
    void should_get_meta_from_extended_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .meta(DummyMeta.MASTER, "yoda")
                .withId("42");

        Assertions.assertThat(tested.meta(DummyMeta.MASTER)).contains("yoda");
    }

    @Test
    void should_convert_extended_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .meta(DummyMeta.MASTER, "yoda")
                .withId("42");
        var actual = tested.convert(l -> new Dummy(l.name() + " kenobi"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(tested.meta(DummyMeta.MASTER)).contains("yoda");
            soft.assertThat(actual.self().name()).isEqualTo("obiwan kenobi");
        });
    }

    @Test
    void should_add_meta_to_extended_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .meta(DummyMeta.MASTER, "yoda")
                .withId("42");
        var actual = tested.withMeta(DummyMeta.FACTION, "jedi");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(tested.meta(DummyMeta.MASTER)).contains("yoda");
            soft.assertThat(actual.meta(DummyMeta.FACTION)).contains("jedi");
        });
    }

    @Test
    void should_fail_to_add_meta_on_extended_entity() {
        var tested = Entity.identify(new Dummy("obiwan"))
                .meta(DummyMeta.MASTER, "yoda")
                .withId("42");
        Assertions.assertThatThrownBy(() -> tested.withMeta(Faction.JEDI, true))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public record Dummy(String name) {
    }

    public enum DummyMeta {FACTION, MASTER;}

    public enum Faction {JEDI, SITH;}
}
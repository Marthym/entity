package fr.ght1pc9kc.entity.graphql.mapper;

import fr.ght1pc9kc.entity.api.Entity;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.Address;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.PersonMetadata;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.PersonWithAddress;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.PersonWithAddressList;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.PersonWithArray;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.PersonWithList;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.PersonWithTypes;
import fr.ght1pc9kc.entity.graphql.mapper.EntitySample.SimplePerson;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultEntityMapperTest {

    private final DefaultEntityMapper tested = new DefaultEntityMapper();

    @Test
    void entityIdentifierProperty_should_return_default_identifier() {
        Assertions.assertThat(tested.entityIdentifierProperty()).isEqualTo("_id");
    }

    @Test
    void toMap_should_return_empty_map_when_entity_is_null() {
        Map<String, Object> result = tested.toMap(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toMap_should_map_id_and_public_fields() {
        Entity<SimplePerson> entity = Entity.identify(new SimplePerson("Obiwan", "Jedi"))
                .withId("42");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("faction")).isEqualTo("Jedi");
            soft.assertThat(result.size()).isEqualTo(3);
        });
    }

    @Test
    void toMap_should_map_fields_and_getters() {
        Entity<EntitySample.PersonWithGetter> entity = Entity.identify(
                new EntitySample.PersonWithGetter("Obiwan", "Jedi", "Lightsaber")
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("faction")).isEqualTo("Jedi");
            soft.assertThat(result.get("weapon")).isEqualTo("Lightsaber");
            soft.assertThat(result.size()).isEqualTo(4);
        });
    }

    @Test
    void toMap_should_ignore_private_fields_without_getter() {
        Entity<EntitySample.PersonWithPrivateField> entity = Entity.identify(
                new EntitySample.PersonWithPrivateField("Obiwan", "secret")
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result).doesNotContainKey("privatePros");
            soft.assertThat(result.size()).isEqualTo(2);
        });
    }

    @Test
    void toMap_should_map_record_components() {
        Entity<EntitySample.PersonRecord> entity = Entity.identify(
                new EntitySample.PersonRecord("Obiwan", "Jedi")
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("faction")).isEqualTo("Jedi");
            soft.assertThat(result.size()).isEqualTo(3);
        });
    }

    @Test
    void toMap_should_include_metadata() {
        Entity<SimplePerson> entity = Entity.identify(new SimplePerson("Obiwan", "Jedi"))
                .meta(PersonMetadata.createdBy, "Lucas")
                .meta(PersonMetadata.version, 1)
                .withId("42");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("_createdBy")).isEqualTo("Lucas");
            soft.assertThat(result.get("_version")).isEqualTo(1);
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("faction")).isEqualTo("Jedi");
            soft.assertThat(result.size()).isEqualTo(5);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void toMap_should_recursively_map_nested_objects() {
        Address address = new Address("Tatooine", "Desert");
        Entity<PersonWithAddress> entity = Entity.identify(
                new PersonWithAddress("Obiwan", address)
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);

        Map<String, Object> addressMap = (Map<String, Object>) result.get("address");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("address")).isInstanceOf(Map.class);
            soft.assertThat(addressMap.get("planet")).isEqualTo("Tatooine");
            soft.assertThat(addressMap.get("terrain")).isEqualTo("Desert");
            soft.assertThat(result.size()).isEqualTo(3);
        });
    }

    @Test
    void toMap_should_convert_list_values() {
        Entity<PersonWithList> entity = Entity.identify(
                new PersonWithList("Obiwan", List.of("Lightsaber", "Comlink"))
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);
        @SuppressWarnings("unchecked")
        List<Object> items = (List<Object>) result.get("items");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("items")).isInstanceOf(List.class);
            soft.assertThat(items).containsExactly("Lightsaber", "Comlink");
        });
    }

    @Test
    void toMap_should_convert_array_values() {
        Entity<PersonWithArray> entity = Entity.identify(
                new PersonWithArray("Obiwan", new String[]{"Lightsaber", "Comlink"})
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);

        @SuppressWarnings("unchecked")
        List<Object> items = (List<Object>) result.get("items");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("items")).isInstanceOf(List.class);
            soft.assertThat(items).containsExactly("Lightsaber", "Comlink");
        });
    }

    @Test
    void toMap_should_convert_nested_objects_in_collections() {
        List<Address> addresses = List.of(
                new Address("Tatooine", "Desert"),
                new Address("Coruscant", "Urban")
        );
        Entity<PersonWithAddressList> entity = Entity.identify(
                new PersonWithAddressList("Obiwan", addresses)
        ).withId("42");

        Map<String, Object> result = tested.toMap(entity);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result.get("addresses");
        @SuppressWarnings("unchecked")
        Map<String, Object> firstAddress = (Map<String, Object>) list.get(0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("addresses")).isInstanceOf(List.class);
            soft.assertThat(list).hasSize(2);
            soft.assertThat(firstAddress.get("planet")).isEqualTo("Tatooine");
            soft.assertThat(firstAddress.get("terrain")).isEqualTo("Desert");
        });
    }

    @Test
    void toMap_should_preserve_simple_types() {
        Entity<PersonWithTypes> entity = Entity.identify(
                new PersonWithTypes("Obiwan", 42, 1.75, true, LocalDate.of(1977, 5, 25))
        ).withId("123");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("123");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("age")).isEqualTo(42);
            soft.assertThat(result.get("height")).isEqualTo(1.75);
            soft.assertThat(result.get("active")).isEqualTo(true);
            soft.assertThat(result.get("birthDate")).isEqualTo(LocalDate.parse("1977-05-25"));
        });
    }

    @Test
    void toMap_should_flatten_map_self() {
        Map<String, Object> self = Map.of(
                "name", "Obiwan",
                "faction", "Jedi",
                "rank", "Master"
        );
        Entity<Map<String, Object>> entity = Entity.identify(self)
                .withId("42");

        Map<String, Object> result = tested.toMap(entity);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(result.get("_id")).isEqualTo("42");
            soft.assertThat(result.get("name")).isEqualTo("Obiwan");
            soft.assertThat(result.get("faction")).isEqualTo("Jedi");
            soft.assertThat(result.get("rank")).isEqualTo("Master");
            soft.assertThat(result.size()).isEqualTo(4);
        });
    }

}
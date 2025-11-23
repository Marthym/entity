package fr.ght1pc9kc.entity.graphql.mapper;

import fr.ght1pc9kc.entity.api.TypedMeta;

import java.time.LocalDate;
import java.util.List;

public class EntitySample {
    public static final class SimplePerson {
        public final String name;
        public final String faction;

        public SimplePerson(String name, String faction) {
            this.name = name;
            this.faction = faction;
        }
    }

    public static final class PersonWithGetter {
        public final String name;
        public final String faction;
        private final String weapon;

        public PersonWithGetter(String name, String faction, String weapon) {
            this.name = name;
            this.faction = faction;
            this.weapon = weapon;
        }

        @SuppressWarnings("unused")
        public String getWeapon() {
            return weapon;
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static final class PersonWithPrivateField {
        public final String name;
        private final String privatePros;

        public PersonWithPrivateField(String name, String password) {
            this.name = name;
            this.privatePros = password;
        }
    }

    public record PersonRecord(String name, String faction) {
    }

    public static final class Address {
        public final String planet;
        public final String terrain;

        public Address(String planet, String terrain) {
            this.planet = planet;
            this.terrain = terrain;
        }
    }

    public static final class PersonWithAddress {
        public final String name;
        public final Address address;

        public PersonWithAddress(String name, Address address) {
            this.name = name;
            this.address = address;
        }
    }

    public static final class PersonWithList {
        public final String name;
        public final List<String> items;

        public PersonWithList(String name, List<String> items) {
            this.name = name;
            this.items = items;
        }
    }

    public static final class PersonWithArray {
        public final String name;
        public final String[] items;

        public PersonWithArray(String name, String[] items) {
            this.name = name;
            this.items = items;
        }
    }

    public static final class PersonWithAddressList {
        public final String name;
        public final List<Address> addresses;

        public PersonWithAddressList(String name, List<Address> addresses) {
            this.name = name;
            this.addresses = addresses;
        }
    }

    public static final class PersonWithTypes {
        public final String name;
        public final int age;
        public final double height;
        public final boolean active;
        public final LocalDate birthDate;

        public PersonWithTypes(String name, int age, double height, boolean active, LocalDate birthDate) {
            this.name = name;
            this.age = age;
            this.height = height;
            this.active = active;
            this.birthDate = birthDate;
        }
    }

    @SuppressWarnings("java:S115")
    public enum PersonMetadata implements TypedMeta {
        createdBy(String.class),
        version(Integer.class);

        private final Class<?> type;

        PersonMetadata(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> type() {
            return this.type;
        }
    }
}

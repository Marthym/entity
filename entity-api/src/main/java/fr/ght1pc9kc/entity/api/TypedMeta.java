package fr.ght1pc9kc.entity.api;

/**
 * <p>Must be implemented by {@link Enum} only to add type to metas data in entity</p>
 * <p>Not mandatory, implementing the interface with un Enum allow to specify the
 * type of each meta data to allow the {@link fr.ght1pc9kc.entity.jackson.serializer.EntityDeserializer}
 * to know how deserialize {@link Object} meta.</p>
 */
@FunctionalInterface
public interface TypedMeta {
    Class<?> type();
}

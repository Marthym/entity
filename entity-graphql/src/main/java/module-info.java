/**
 * Jackson module to add serializer and deserializer for Entity
 */
module fr.ght1pc9kc.entity.graphql {
    requires fr.ght1pc9kc.entity.api;

    requires static lombok;

    requires com.graphqljava;
    requires java.desktop;

    exports fr.ght1pc9kc.entity.graphql;
}
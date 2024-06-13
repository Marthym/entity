/**
 * Jackson module to add serializer and deserializer for Entity
 */
module fr.ght1pc9kc.entity.graphql {
    requires fr.ght1pc9kc.entity.api;
    requires fr.ght1pc9kc.entity.jackson;

    requires static lombok;
    requires org.slf4j;

    requires com.fasterxml.jackson.databind;
    requires com.graphqljava;

    exports fr.ght1pc9kc.entity.graphql;
}
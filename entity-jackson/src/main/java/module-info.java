/**
 * Jackson module to add serializer and deserializer for Entity
 */
module fr.ght1pc9kc.entity.jackson {
    requires fr.ght1pc9kc.entity.api;

    requires static lombok;
    requires org.slf4j;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports fr.ght1pc9kc.entity.jackson;
}
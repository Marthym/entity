/**
 * Jackson 3 module to add serializer and deserializer for Entity
 */
module fr.ght1pc9kc.entity.json {
    requires fr.ght1pc9kc.entity.api;

    requires static lombok;

    requires tools.jackson.core;
    requires tools.jackson.databind;

    exports fr.ght1pc9kc.entity.json;
}
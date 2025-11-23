/**
 * Module for access API classes
 */
module fr.ght1pc9kc.entity.api {
    requires static lombok;
    requires static org.jspecify;

    exports fr.ght1pc9kc.entity.api;
    exports fr.ght1pc9kc.entity.api.builders;
    exports fr.ght1pc9kc.entity.api.impl;
    exports fr.ght1pc9kc.entity.api.mapper;
}
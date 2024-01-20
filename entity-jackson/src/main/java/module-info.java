module entity.jackson {
    requires entity.api;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports fr.ght1pc9kc.entity.jackson;
}
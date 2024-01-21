package fr.ght1pc9kc.entity.jackson.samples;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LightSaber.class, name = "LIGHT"),
        @JsonSubTypes.Type(value = DarkSaber.class, name = "DARK")
})
public sealed interface Saber permits DarkSaber, LightSaber {
    Color color();
}

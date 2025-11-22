package fr.ght1pc9kc.entity.jackson.samples;

public record Simple(String message) {
    @Override
    public String toString() {
        return "SIMPLE_WRAPPED";
    }
}

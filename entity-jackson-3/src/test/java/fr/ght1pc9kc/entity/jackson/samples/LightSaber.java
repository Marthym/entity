package fr.ght1pc9kc.entity.jackson.samples;

public record LightSaber(
        Color color,
        int blade
) implements Saber {
    @Override
    public String toString() {
        return "LIGHTSABER";
    }
}

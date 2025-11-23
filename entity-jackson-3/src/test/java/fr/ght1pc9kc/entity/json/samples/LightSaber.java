package fr.ght1pc9kc.entity.json.samples;

public record LightSaber(
        Color color,
        int blade
) implements Saber {
    @Override
    public String toString() {
        return "LIGHTSABER";
    }
}

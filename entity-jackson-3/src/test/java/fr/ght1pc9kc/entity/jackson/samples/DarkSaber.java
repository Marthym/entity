package fr.ght1pc9kc.entity.jackson.samples;

public record DarkSaber(
        Color color
) implements Saber {
    public DarkSaber {
        color = Color.SHADOW;
    }

    @Override
    public String toString() {
        return "DARKSABER";
    }
}

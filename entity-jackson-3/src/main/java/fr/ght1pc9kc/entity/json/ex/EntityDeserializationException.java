package fr.ght1pc9kc.entity.json.ex;

public class EntityDeserializationException extends RuntimeException {
    public EntityDeserializationException(String message) {
        super(message);
    }

    public EntityDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

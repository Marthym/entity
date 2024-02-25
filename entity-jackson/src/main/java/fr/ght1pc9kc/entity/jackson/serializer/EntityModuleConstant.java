package fr.ght1pc9kc.entity.jackson.serializer;

public abstract class EntityModuleConstant {
    public static final String META_PREFIX = "_";
    public static final String IDENTIFIER = META_PREFIX + "id";
    public static final String SELF_PROPERTY = META_PREFIX + "self";

    private EntityModuleConstant() {
    }
}

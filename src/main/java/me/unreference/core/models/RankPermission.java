package me.unreference.core.models;

public class RankPermission {
    private final boolean IS_INHERITABLE;

    public RankPermission(boolean isInheritable) {
        this.IS_INHERITABLE = isInheritable;
    }

    public boolean isInheritable() {
        return IS_INHERITABLE;
    }
}

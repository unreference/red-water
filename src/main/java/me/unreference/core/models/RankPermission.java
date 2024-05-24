package me.unreference.core.models;

public class RankPermission {
    private final boolean RANK_PERMISSION_IS_INHERITABLE;

    public RankPermission(boolean isInheritable) {
        this.RANK_PERMISSION_IS_INHERITABLE = isInheritable;
    }

    public boolean isInheritable() {
        return RANK_PERMISSION_IS_INHERITABLE;
    }

}

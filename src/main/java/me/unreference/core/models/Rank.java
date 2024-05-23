package me.unreference.core.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Rank {
    PLAYER("player", null, NamedTextColor.WHITE, NamedTextColor.YELLOW),

    TRAINEE("trainee", "Trainee", NamedTextColor.BLUE, NamedTextColor.YELLOW, PLAYER),
    MOD("mod", "Mod", NamedTextColor.GOLD, NamedTextColor.YELLOW, TRAINEE),
    SM("sm", "Sr.Mod", NamedTextColor.GOLD, NamedTextColor.YELLOW, MOD),
    ADMIN("admin", "Admin", NamedTextColor.DARK_RED, NamedTextColor.YELLOW, SM),
    LT("lt", "Leader", NamedTextColor.DARK_RED, NamedTextColor.YELLOW, ADMIN),
    OWNER("owner", "Owner", NamedTextColor.DARK_RED, NamedTextColor.YELLOW, LT);

    private final String RANK_ID;
    private final String RANK_DISPLAY_NAME;
    private final NamedTextColor RANK_PREFIX_COLOR;
    private final NamedTextColor RANK_NAME_COLOR;
    private final Rank RANK_PARENT;
    private final Map<String, RankPermission> RANK_PERMISSIONS_GRANTED;
    private final Set<String> RANK_PERMISSIONS_REVOKED;

    Rank(String id, String display, NamedTextColor prefixColor, NamedTextColor nameColor) {
        this.RANK_ID = id;
        this.RANK_DISPLAY_NAME = display;
        this.RANK_PREFIX_COLOR = prefixColor;
        this.RANK_NAME_COLOR = nameColor;
        this.RANK_PARENT = null;
        this.RANK_PERMISSIONS_GRANTED = new HashMap<>();
        this.RANK_PERMISSIONS_REVOKED = new HashSet<>();
    }

    Rank(String id, String display, NamedTextColor prefixColor, NamedTextColor nameColor, Rank parent) {
        this.RANK_ID = id;
        this.RANK_DISPLAY_NAME = display;
        this.RANK_PREFIX_COLOR = prefixColor;
        this.RANK_NAME_COLOR = nameColor;
        this.RANK_PARENT = parent;
        this.RANK_PERMISSIONS_GRANTED = new HashMap<>();
        this.RANK_PERMISSIONS_REVOKED = new HashSet<>();
    }

    public String getId() {
        return RANK_ID;
    }

    public Component getDisplay() {
        if (RANK_DISPLAY_NAME == null) {
            return Component.text("", RANK_NAME_COLOR);
        }

        return Component.text().append(
                        Component.text(RANK_DISPLAY_NAME.toUpperCase() + " ", RANK_PREFIX_COLOR)
                                .decorate(TextDecoration.BOLD),
                        Component.text("", RANK_NAME_COLOR))
                .build();
    }

    public NamedTextColor getPrefixColor() {
        return RANK_PREFIX_COLOR;
    }

    public NamedTextColor getNameColor() {
        return RANK_NAME_COLOR;
    }

    public void grantPermission(String permission, boolean isInheritable) {
        RANK_PERMISSIONS_GRANTED.put(permission, new RankPermission(isInheritable));
        RANK_PERMISSIONS_REVOKED.remove(permission);

    }

    public void revokePermission(String permission) {
        RANK_PERMISSIONS_GRANTED.remove(permission);
        RANK_PERMISSIONS_REVOKED.add(permission);
    }

    public boolean isPermitted(String permission) {
        if (RANK_PERMISSIONS_REVOKED.contains(permission)) {
            return false;
        }

        RankPermission rankPermission = RANK_PERMISSIONS_GRANTED.get(permission);
        if (rankPermission != null) {
            return rankPermission.isInheritable() && RANK_PARENT != null && !RANK_PARENT.isPermitted(permission);
        }

        if (RANK_PARENT != null) {
            return RANK_PARENT.isPermitted(permission);
        }

        return false;
    }
}

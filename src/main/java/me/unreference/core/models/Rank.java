package me.unreference.core.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.unreference.core.utils.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Rank {
  PLAYER("player", null, NamedTextColor.YELLOW),
  ULTRA("ultra", "&b&lULTRA", NamedTextColor.YELLOW, PLAYER),
  HERO("hero", "&d&lHERO", NamedTextColor.YELLOW, ULTRA),
  LEGEND("legend", "&a&lLEGEND", NamedTextColor.YELLOW, HERO),
  TITAN("titan", "&c&lTITAN", NamedTextColor.YELLOW, LEGEND),
  ETERNAL("eternal", "&3&lETERNAL", NamedTextColor.YELLOW, TITAN),
  IMMORTAL("immortal", "&e&lIMMORTAL", NamedTextColor.BLUE, ETERNAL),

  TRAINEE("trainee", "&6&lTRAINEE", NamedTextColor.YELLOW, IMMORTAL),
  MOD("mod", "&6&lMOD", NamedTextColor.YELLOW, TRAINEE),
  SM("srmod", "&6&lSR.MOD", NamedTextColor.YELLOW, MOD),

  ADMIN("admin", "&4&lADMIN", NamedTextColor.YELLOW, SM),
  DEV("dev", "&4&lDEV", NamedTextColor.YELLOW, ADMIN),
  LT("leader", "&4&lLEADER", NamedTextColor.YELLOW, DEV),
  OWNER("owner", "&4&lOWNER", NamedTextColor.YELLOW, LT);

  private final String RANK_ID;
  private final String RANK_PREFIX;
  private final NamedTextColor RANK_PLAYER_NAME_COLOR;
  private final Rank RANK_PARENT;

  private final Map<String, RankPermission> RANK_PERMISSIONS_GRANTED;
  private final Set<String> RANK_PERMISSIONS_REVOKED;

  Rank(String id, String prefixFormat, NamedTextColor playerNameColor) {
    this.RANK_ID = id;
    this.RANK_PREFIX = prefixFormat;
    this.RANK_PLAYER_NAME_COLOR = playerNameColor;
    this.RANK_PARENT = null;
    this.RANK_PERMISSIONS_GRANTED = new HashMap<>();
    this.RANK_PERMISSIONS_REVOKED = new HashSet<>();
  }

  Rank(String id, String prefixFormat, NamedTextColor playerNameColor, Rank parent) {
    this.RANK_ID = id;
    this.RANK_PREFIX = prefixFormat;
    this.RANK_PLAYER_NAME_COLOR = playerNameColor;
    this.RANK_PARENT = parent;
    this.RANK_PERMISSIONS_GRANTED = new HashMap<>();
    this.RANK_PERMISSIONS_REVOKED = new HashSet<>();
  }

  public String getId() {
    return RANK_ID;
  }

  public Component getPrefixFormatting() {
    if (RANK_PREFIX != null) {
      return FormatUtil.getFormattedComponent(RANK_PREFIX).appendSpace();
    }

    return Component.empty();
  }

  public NamedTextColor getPlayerNameColor() {
    return RANK_PLAYER_NAME_COLOR;
  }

  public void grantPermission(String permission, boolean isInheritable) {
    RANK_PERMISSIONS_GRANTED.put(permission, new RankPermission(isInheritable));
    synchronizePermissions(permission);
  }

  public void revokePermission(String permission) {
    RANK_PERMISSIONS_GRANTED.remove(permission);
    RANK_PERMISSIONS_REVOKED.add(permission);
    synchronizePermissions(permission);
  }

  public boolean isPermitted(String permission) {
    // Check if the permission is explicitly revoked for this rank
    if (RANK_PERMISSIONS_REVOKED.contains(permission)) {
      return false;
    }

    // Check if the permission is explicitly granted for this rank
    RankPermission rankPermission = RANK_PERMISSIONS_GRANTED.get(permission);
    if (rankPermission != null) {
      return true; // Explicitly granted permission
    }

    // Check if the permission is inheritable from parent ranks
    if (RANK_PARENT != null && RANK_PARENT.isPermitted(permission)) {
      RankPermission parentRankPermission = RANK_PARENT.RANK_PERMISSIONS_GRANTED.get(permission);
      if (parentRankPermission != null && parentRankPermission.isInheritable()) {
        // Inherit permission from parent rank
        RANK_PERMISSIONS_GRANTED.put(permission, parentRankPermission);
        return true;
      }
    }

    // Permission not explicitly granted or inheritable from parent rank
    return false;
  }

  private void synchronizePermissions(String permission) {
    if (RANK_PARENT != null) {
      if (RANK_PARENT.isPermitted(permission)) {
        if (!RANK_PERMISSIONS_GRANTED.containsKey(permission)) {
          RANK_PERMISSIONS_GRANTED.put(permission, new RankPermission(true));
        } else {
          RANK_PERMISSIONS_GRANTED.remove(permission);
        }
      }
    }
  }
}

package me.unreference.core.managers;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import me.unreference.core.models.Rank;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class RankManager {
    private static RankManager instance;
    private final File RANK_PLAYER_FILE;
    private final FileConfiguration RANK_PLAYER_CONFIG;

    private RankManager() {
        this.RANK_PLAYER_FILE = new File("plugins/Core/players.yml");
        this.RANK_PLAYER_CONFIG = YamlConfiguration.loadConfiguration(RANK_PLAYER_FILE);
    }

    public static RankManager getInstance() {
        if (instance == null) {
            instance = new RankManager();
        }

        return instance;
    }

    public Rank getPlayerRank(Player player) {
        String playerId = player.getUniqueId().toString();
        String rankId = RANK_PLAYER_CONFIG.getString(playerId);

        if (rankId != null) {
            return getRankFromId(rankId);
        }

        return null;
    }

    public void setPlayerRank(Player player, Rank newRank) {
        String playerId = player.getUniqueId().toString();
        String groupId = newRank.getId();

        RANK_PLAYER_CONFIG.set(playerId, groupId);
        savePlayerDataConfig();
    }

    public Rank getRankFromId(String id) {
        for (Rank group : Rank.values()) {
            if (group.getId().equalsIgnoreCase(id)) {
                return group;
            }
        }

        return null;
    }

    private void savePlayerDataConfig() {
        try {
            RANK_PLAYER_CONFIG.save(RANK_PLAYER_FILE);
        } catch (IOException e) {
            PaperPluginLogger.getAnonymousLogger().severe("Error saving player data configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

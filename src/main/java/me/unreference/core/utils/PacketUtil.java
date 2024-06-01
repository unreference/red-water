package me.unreference.core.utils;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {

  public static void broadcastPacket(Packet<?> packet) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      sendPacket(player, packet);
    }
  }

  public static void sendPacket(Player player, Packet<?> packet) {
    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
    ServerGamePacketListenerImpl connection = serverPlayer.connection;
    connection.send(packet);
  }
}

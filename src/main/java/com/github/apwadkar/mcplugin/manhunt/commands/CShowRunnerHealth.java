package com.github.apwadkar.mcplugin.manhunt.commands;

import com.github.apwadkar.mcplugin.ServerPlugin;
import net.minecraft.server.v1_16_R2.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.apwadkar.mcplugin.NMS.getNMSClass;
import static com.github.apwadkar.mcplugin.NMS.sendPacket;
import static net.minecraft.server.v1_16_R2.ChatMessageType.GAME_INFO;

public class CShowRunnerHealth implements CommandExecutor, Listener {

    Map<Player, BukkitTask> taskMap;

    public CShowRunnerHealth() {
        this.taskMap = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player sender = Bukkit.getPlayer(commandSender.getName());
        if (sender != null && sender.getGameMode() == GameMode.SPECTATOR) {
            if (args[0].toLowerCase().equals("start")) {
                if (args.length != 2) {
                    return false;
                }
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        double health = Bukkit.getPlayer(args[1]).getHealth();

                        Class<?> chatBaseComponent = getNMSClass("IChatBaseComponent");
                        Class<?> chatComponentText = getNMSClass("ChatComponentText");
                        Class<?> packetPlayOutChat = getNMSClass("PacketPlayOutChat");
                        try {
                            String message = ChatColor
                                    .translateAlternateColorCodes('&',
                                            "&" + ChatColor.DARK_AQUA.getChar() + args[1] + "'s Health: " + health);
                            Object cct = chatComponentText.getDeclaredConstructor(String.class)
                                    .newInstance(message);
                            Object ppoc = packetPlayOutChat.getDeclaredConstructor(chatBaseComponent, ChatMessageType.class, UUID.class)
                                    .newInstance(cct, GAME_INFO, sender.getUniqueId());
                            sendPacket(sender, ppoc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskTimer(ServerPlugin.getPlugin(), 0L, 2L);
                taskMap.put(sender, task);
            } else if (args[0].toLowerCase().equals("stop")) {
                if (args.length != 1) {
                    return false;
                }
                BukkitTask task = taskMap.get(sender);
                if (task != null) {
                    task.cancel();
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage("You must be in spectator mode");
        }
        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        BukkitTask task = taskMap.get(p);
        if (task != null) {
            task.cancel();
        }
    }

}

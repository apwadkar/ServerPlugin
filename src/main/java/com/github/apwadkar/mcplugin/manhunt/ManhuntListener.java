package com.github.apwadkar.mcplugin.manhunt;

import com.github.apwadkar.mcplugin.ServerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManhuntListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.hasItem() && event.getItem().getType() == Material.COMPASS) {
                Player hunter = ManhuntUtils.getRunner(event.getPlayer().getWorld());
                ItemMeta meta = event.getItem().getItemMeta();
                if (meta instanceof CompassMeta && hunter != null) {
                    ((CompassMeta) meta).setLodestone(hunter.getLocation());
                    ((CompassMeta) meta).setLodestoneTracked(false);
                    Inventory inv = event.getPlayer().getInventory();
                    inv.getItem(inv.first(event.getItem())).setItemMeta(meta);
                    event.getPlayer().sendMessage("You are now tracking " + hunter.getName());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!isPlayerHunter(player)) {
            player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
        }
    }

    public boolean isPlayerHunter(Player player) {
        ServerPlugin plugin = ServerPlugin.getPlugin();
        Connection connection = plugin.getConnection();
        String sql = "select hunter\n" +
                "from manhunt\n" +
                "where world = ? and name = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getWorld().getName().split("_")[0]);
            statement.setString(2, player.getName());
            ResultSet res = statement.executeQuery();
            return res.getBoolean("hunter");
        } catch (SQLException e) {
            return false;
//            plugin.getLogger().info("SQLException: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!isPlayerHunter(player)) {
            ServerPlugin plugin = ServerPlugin.getPlugin();
            Connection connection = plugin.getConnection();
            String sql = "replace into manhunt (name, world, hunter) \n" +
                    "values(?, ?, false);";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.getName());
                statement.setString(2, player.getWorld().getName().split("_")[0]);
                statement.executeUpdate();
                player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
            } catch (SQLException e) {
                Bukkit.getLogger().info("SQLException: " + e.getMessage());
            }
        }
    }

}

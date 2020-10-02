package com.github.apwadkar.mcplugin.manhunt.commands;

import com.github.apwadkar.mcplugin.ServerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CRemoveHunter implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("You must specify a player name");
            return false;
        }
        Connection connection = ServerPlugin.getPlugin().getConnection();
        String sql = "replace into manhunt (name, world, hunter) \n" +
                "values(?, ?, false);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getName());
            statement.setString(2, player.getWorld().getName().split("_")[0]);
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info("SQLException: " + e.getMessage());
        }
        return true;
    }
}

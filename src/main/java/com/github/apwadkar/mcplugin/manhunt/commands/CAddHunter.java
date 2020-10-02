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

public class CAddHunter implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage("You must specify a player name");
            return false;
        }
        Connection connection = ServerPlugin.getPlugin().getConnection();
        String sql = "replace into manhunt (name, world, hunter) \n" +
                "values(?, ?, true);";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            String worldName = player.getWorld().getName().split("_")[0];
            statement.setString(1, player.getName());
            statement.setString(2, worldName);
            statement.execute();

            statement = connection.prepareStatement("replace into manhunt_worlds (world) values(?)");
            statement.setString(1, worldName);
            statement.execute();
        } catch (SQLException e) {
            ServerPlugin.getPlugin().getLogger().info("SQLException: " + e.getMessage());
        }
        return true;
    }
}

package com.github.apwadkar.mcplugin.manhunt;

import com.github.apwadkar.mcplugin.ServerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManhuntUtils {

    public static Player getRunner(World world) {
        Connection conn = ServerPlugin.getPlugin().getConnection();
        String sql = "select name\n" +
                "from manhunt\n" +
                "where world = ? and hunter is true;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(0, world.getName());
            ResultSet rs = stmt.executeQuery();
            return Bukkit.getPlayer(rs.getString("name"));
        } catch (SQLException e) {
            Bukkit.getLogger().info("SQLException: " + e.getMessage());
        }
        return null;
    }

}

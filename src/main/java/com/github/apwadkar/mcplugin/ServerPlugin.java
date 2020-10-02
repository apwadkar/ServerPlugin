package com.github.apwadkar.mcplugin;

import com.github.apwadkar.mcplugin.entities.commands.CSpawnNPC;
import com.github.apwadkar.mcplugin.manhunt.ManhuntListener;
import com.github.apwadkar.mcplugin.manhunt.commands.CAddHunter;
import com.github.apwadkar.mcplugin.manhunt.commands.CRemoveHunter;
import com.github.apwadkar.mcplugin.manhunt.commands.CShowRunnerHealth;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class ServerPlugin extends JavaPlugin {

    private static ServerPlugin plugin;

    private Connection connection;

    public static ServerPlugin getPlugin() {
        return plugin;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("ServerPlugin is enabled!");
        getServer().getPluginManager().registerEvents(new ManhuntListener(), this);
        this.getCommand("addHunter").setExecutor(new CAddHunter());
        this.getCommand("removeHunter").setExecutor(new CRemoveHunter());
        CShowRunnerHealth runnerHealth = new CShowRunnerHealth();
        getServer().getPluginManager().registerEvents(runnerHealth, this);
        this.getCommand("showHealth").setExecutor(runnerHealth);
        this.getCommand("spawnNPC").setExecutor(new CSpawnNPC());

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:players.db");
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                getLogger().info("Driver name is " + meta.getDriverName());
                getLogger().info("A new database has been created");

                Statement statement = connection.createStatement();
                statement.execute("create table if not exists manhunt (\n" +
                        "    name text not null,\n" +
                        "    world text not null default 'world',\n" +
                        "    hunter integer default false\n" +
                        ");");
                statement.execute("create table if not exists manhunt_worlds (world text not null default 'world');");
            }
        } catch (SQLException e) {
            getLogger().info("Unable to connect to SQLite database");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("ServerPlugin is disabled!");
    }

}

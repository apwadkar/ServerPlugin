package com.github.apwadkar.mcplugin.entities.commands;

import com.github.apwadkar.mcplugin.entities.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CSpawnNPC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length != 1) {
            return false;
        }
        Player p = Bukkit.getPlayer(commandSender.getName());
        NPC npc = new NPC(p.getLocation(), args[0]);
        npc.spawn(p);
        return true;
    }
}

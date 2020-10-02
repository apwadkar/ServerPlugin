package com.github.apwadkar.mcplugin.entities;

import com.github.apwadkar.mcplugin.entities.gui.NPCGui;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class NPC implements Listener {

    public EntityPlayer entity;
    private final GameProfile gameProfile;
    private final Location location;
    private final NPCGui gui;

    public NPC(Location location, String name) {
        this.gameProfile = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000000"), name);
        this.location = location;
        this.gui = new NPCGui(name + "'s Inventory");

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer ws = ((CraftWorld) this.location.getWorld()).getHandle();
        this.entity = new EntityPlayer(server, ws, this.gameProfile, new PlayerInteractManager(ws));

        this.entity.playerConnection = new PlayerConnection(server, new NetworkManager(EnumProtocolDirection.CLIENTBOUND), this.entity);
        this.entity.setHealth(1.f);
        ws.addEntity(this.entity);
        this.entity.setLocation(this.location.getX(), this.location.getY(), this.location.getZ(),
                this.location.getYaw(), this.location.getPitch());
    }

    public void spawn(Player player) {
        PlayerConnection pc = ((CraftPlayer) player).getHandle().playerConnection;
        pc.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.entity));
        pc.sendPacket(new PacketPlayOutNamedEntitySpawn(this.entity));
        pc.sendPacket(new PacketPlayOutEntityHeadRotation(this.entity, (byte) ((this.location.getYaw() * 256.0F) / 360.0F)));
        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().players
                .removeIf(e -> e.getUniqueID().equals(this.entity.getUniqueID()));
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().equals(this.entity)) {
            event.setCancelled(true);
            this.gui.openInventory((HumanEntity) this.entity);
        }
    }

}

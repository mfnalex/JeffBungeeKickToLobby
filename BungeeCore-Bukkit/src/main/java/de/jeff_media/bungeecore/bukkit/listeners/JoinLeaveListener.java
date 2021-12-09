package de.jeff_media.bungeecore.bukkit.listeners;

import de.jeff_media.bungeecore.bukkit.BungeeCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    private final BungeeCore main;

    public JoinLeaveListener(BungeeCore main) {
        this.main=main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}

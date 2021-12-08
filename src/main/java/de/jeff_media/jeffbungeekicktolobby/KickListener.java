package de.jeff_media.jeffbungeekicktolobby;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class KickListener implements Listener {
    private final JeffBungeeKickToLobby main;

    public KickListener(JeffBungeeKickToLobby main) {
        this.main=main;
    }

    @EventHandler
    public void onKick(ServerKickEvent event) {
        ServerInfo fallback = main.getProxy().getServerInfo(main.getConfig().getString("fallback","lobby"));
        if(fallback == null) return;
        ServerInfo current = event.getKickedFrom();
        if(current.equals(fallback)) return;
        event.setCancelServer(fallback);
        return;
    }
}

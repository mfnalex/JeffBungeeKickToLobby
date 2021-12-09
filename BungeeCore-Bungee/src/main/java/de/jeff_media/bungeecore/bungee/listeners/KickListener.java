package de.jeff_media.bungeecore.bungee.listeners;

import de.jeff_media.bungeecore.bungee.BungeeCore;
import de.jeff_media.bungeecore.bungee.jefflib.TinyTextUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class KickListener implements Listener {
    private final BungeeCore main;
    private final Configuration config;
    private final Set<Pattern> kickReasons = new HashSet<>();


    public KickListener(BungeeCore main) {
        this.main=main;
        this.config = main.loadConfig("kick-fallback.yml");


        config.getStringList("reasons").forEach(reason -> kickReasons.add(Pattern.compile(reason)));
    }


    @EventHandler
    public void onKick(ServerKickEvent event) {
        if(!config.getBoolean("enabled")) return;
        final ServerInfo fallback = main.getProxy().getServerInfo(config.getString("server","lobby"));
        if(fallback == null) return;
        final ServerInfo current = event.getKickedFrom();
        if(current.equals(fallback)) return;
        final String reason = BaseComponent.toPlainText(event.getKickReasonComponent());
        if(kickReasons.stream().anyMatch(pattern -> pattern.matcher(reason).matches())) {
            event.setCancelServer(fallback);
            event.setCancelled(true);
            if(config.getBoolean("send-message")) {
                config.getStringList("message").forEach(line -> {
                    event.getPlayer().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(TinyTextUtils.format(line
                            .replace("%player%",event.getPlayer().getName())
                            .replace("%from%",current.getName())
                            .replace("%to%", fallback.getName()))));
                });
            }
        }
    }
}

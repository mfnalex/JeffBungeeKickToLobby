package de.jeff_media.bungeecore.bungee.listeners;

import de.jeff_media.bungeecore.bungee.DiscordChannel;
import de.jeff_media.bungeecore.bungee.BungeeCore;
import de.jeff_media.bungeecore.bungee.jefflib.TinyTextUtils;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JoinLeaveListener implements Listener {

    private final Configuration config;

    private enum MessageType {
        JOIN_CHAT("join-message-chat", true),
        JOIN_DISCORD("join-message-discord", false),
        LEAVE_CHAT("leave-message-chat", true),
        LEAVE_DISCORD("leave-message-discord", false);

        @Getter private final String nameInConfig;
        @Getter private final boolean shouldFormat;

        MessageType(String nameInConfig, boolean shouldFormat) {
            this.nameInConfig = nameInConfig;
            this.shouldFormat = shouldFormat;
        }
    }

    private final BungeeCore main;

    public JoinLeaveListener(BungeeCore main) {
        this.main = main;
        this.config = main.loadConfig("join-leave-messages.yml");
    }

    private boolean isEnabled() {
        return config.getBoolean("enabled");
    }

    private void sendDiscordMessage(String message) {
        ProxyServer.getInstance().getScheduler().runAsync(main, () -> {
            main.getDiscordManager().send(DiscordChannel.JOIN_LEAVE_MESSAGES,message);
        });
    }

    private String getMessage(MessageType type, ProxiedPlayer player) {
        return config.getStringList(type.getNameInConfig()).stream().map(line -> {
            line = line
                    .replace("%player%",player.getName());
            return TinyTextUtils.format(line);
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if(!isEnabled()) return;
        ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().getScheduler().schedule(main, () -> {
            if(!player.isConnected()) return;
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(getMessage(MessageType.JOIN_CHAT, player)));
            sendDiscordMessage(getMessage(MessageType.JOIN_DISCORD, player));
        }, 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        if(!isEnabled()) return;
        ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(getMessage(MessageType.LEAVE_CHAT, player)));
        sendDiscordMessage(getMessage(MessageType.LEAVE_DISCORD,player));
    }
}

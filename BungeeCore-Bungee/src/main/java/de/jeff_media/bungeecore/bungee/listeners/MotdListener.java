package de.jeff_media.bungeecore.bungee.listeners;

import de.jeff_media.bungeecore.bungee.BungeeCore;
import de.jeff_media.bungeecore.bungee.jefflib.TinyTextUtils;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MotdListener implements Listener {

    private final BungeeCore main;
    private final Configuration config;
    private BufferedImage favicon;

    public MotdListener(BungeeCore main) {
        this.main = main;
        this.config = main.loadConfig("motd.yml");
        if(config.getBoolean("server-icon")) {
            File iconFile = new File(main.getDataFolder().getParentFile().getParentFile(), "server-icon.png");
            if (iconFile.exists()) {
                try {
                    favicon = ImageIO.read(iconFile);
                } catch (IOException e) {
                    main.getLogger().warning("Could not read your server-icon.png file");
                }
            }
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        if(!config.getBoolean("enabled")) return;
        ServerPing ping = event.getResponse();
        ping.setPlayers(new ServerPing.Players(config.getInt("max-players"), ProxyServer.getInstance().getOnlineCount(),ping.getPlayers().getSample()));
        String motd = config.getString("motd");
        if(motd == null || motd.length()==0) return;
        motd = motd.replace("%countdown%", getCountdown());
        BaseComponent[] components = TextComponent.fromLegacyText(TinyTextUtils.format(motd));
        BaseComponent component = components[0];
        for(int i = 1; i < components.length; i++) {
            component.addExtra(components[i]);
        }
        ping.setDescriptionComponent(component);
        if(favicon != null) {
            ping.setFavicon(Favicon.create(favicon));
        }
        event.setResponse(ping);
    }

    private String getCountdown() {
        long timestamp = config.getLong("countdown");
        if(System.currentTimeMillis()/1000 >= timestamp) {
            return config.getString("countdown-text-after");
        } else {
            return config.getString("countdown-text-before").replace("%timeleft%",getTimeLeft());
        }
    }

    private String getTimeLeft() {
        long timestamp = config.getLong("countdown");

        long seconds = Math.abs(System.currentTimeMillis()/1000-timestamp);
        if(seconds < 60) return seconds + " seconds";
        long minutes = seconds / 60;
        if(minutes < 60) return minutes + " minutes";
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if(hours < 24) return hours + " hours " + remainingMinutes + " minutes";
        long days = hours / 24;
        long remainingHours = days % 24;
        if(days < 2) return days + " days " + remainingHours + " hours";
        return days + " days";
    }
}

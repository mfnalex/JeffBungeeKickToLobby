package de.jeff_media.bungeecore.bukkit;

import de.jeff_media.bungeecore.bukkit.listeners.JoinLeaveListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BungeeCore extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(this),this);
    }
}

package de.jeff_media.bungeecore.bungee;

import de.jeff_media.bungeecore.bungee.listeners.JoinLeaveListener;
import de.jeff_media.bungeecore.bungee.listeners.KickListener;
import co.aikar.commands.BungeeCommandManager;
import de.jeff_media.bungeecore.bungee.listeners.MotdListener;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BungeeCore extends Plugin {

    @Getter private static ConfigurationProvider yamlConfigurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    @Getter private DiscordManager discordManager;

    private Configuration saveDefaultConfig(File file, String fileName) {
        Configuration defaultConfig = yamlConfigurationProvider.load(getResourceAsStream(fileName));
        if(!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                yamlConfigurationProvider.save(defaultConfig, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return defaultConfig;
    }

    public Configuration loadConfig(String fileName) {
        File file = new File(getDataFolder(), fileName);
        Configuration defaultConfig = saveDefaultConfig(file, fileName);
        try {
            Configuration config = yamlConfigurationProvider.load(file);
            for(String key : defaultConfig.getKeys()) {
                if(config.contains(key)) {
                    System.out.println("Loaded value: " + fileName + ", " + key + ": " + config.get(key));
                    continue;
                }
                System.out.println(fileName + " doesn't have any value for " + key + ", using default value " + defaultConfig.get(key));
                config.set(key, defaultConfig.get(key));
            }
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            return defaultConfig;
        }
    }

    public void reload() {
        discordManager = new DiscordManager(this);

        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().registerListener(this, new KickListener(this));
        getProxy().getPluginManager().registerListener(this, new JoinLeaveListener(this));
        getProxy().getPluginManager().registerListener(this, new MotdListener(this));
    }

    @Override
    public void onEnable() {
        reload();

        BungeeCommandManager acf = new BungeeCommandManager(this);
        acf.registerCommand(new ReloadCommand(this));

        getProxy().getScheduler().schedule(this, this::showDebugOnDiscord,2, TimeUnit.SECONDS);
    }

    private void showDebugOnDiscord() {
        List<String> msg = new ArrayList<>();
        String plugin = this.getDescription().getName();
        String version = this.getDescription().getVersion();
        String servers = getProxy().getServers().values().stream().map(serverInfo -> serverInfo.getName()).collect(Collectors.joining(", "));

        msg.add(getProxy().getName() + " v" + getProxy().getVersion() + " started.");
        msg.add(plugin + " v" + version + " enabled.");
        msg.add("Servers on this proxy: " + servers);

        getDiscordManager().send(DiscordChannel.DEBUG_MESSAGES, msg.stream().collect(Collectors.joining(System.lineSeparator())));
    }

    @Override
    public void onDisable() {
        discordManager.shutdown();
    }

}

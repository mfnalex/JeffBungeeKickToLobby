package de.jeff_media.jeffbungeekicktolobby;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class JeffBungeeKickToLobby extends Plugin {

    @Getter private final ConfigurationProvider yamlConfigurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    @Getter private final Configuration config;
    @Getter private final File configFile;

    {
        config = yamlConfigurationProvider.load(getResourceAsStream("config.yml"));
        configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            try {
                yamlConfigurationProvider.save(config, configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new KickListener(this));
    }

}

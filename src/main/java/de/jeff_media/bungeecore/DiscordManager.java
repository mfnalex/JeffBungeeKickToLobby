package de.jeff_media.bungeecore;

import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.md_5.bungee.config.Configuration;

public class DiscordManager {

    private final BungeeCore main;
    @Getter
    private static JDA discord;
    private final Configuration config;

    @SneakyThrows
    public DiscordManager(BungeeCore main) {
        if(discord != null) discord.shutdownNow();
        this.main = main;
        this.config = main.loadConfig("discord.yml");
        if (config.getBoolean("enabled")) {
            this.discord = JDABuilder.createDefault(config.getString("bot-token")).build();
        } else {
            this.discord = null;
        }
    }

    public void send(DiscordChannel channel, String message) {
        if (discord == null) return;
        if (!config.getBoolean(channel.getNameInConfig() + ".enabled")) return;
        long channelId = config.getLong(channel.getNameInConfig() + ".channel",0);


        try {
            TextChannel textChannel = discord.getTextChannelById(channelId);
            if (textChannel != null) {
                MessageAction act = textChannel.sendMessage(message);
                act.queue();
            } else {
                main.getLogger().warning("Channel does not exist or not accessible by the bot: " + channelId + " (" + channel.getNameInConfig() + ")");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void shutdown() {
        if(discord != null) {
            discord.shutdownNow();
        }
    }
}

package de.jeff_media.bungeecore.bungee;

import lombok.Getter;

public enum DiscordChannel {

    JOIN_LEAVE_MESSAGES("join-leave-messages"),
    DEBUG_MESSAGES("debug");

    @Getter private final String nameInConfig;

    private DiscordChannel(String nameInConfig) {
        this.nameInConfig = nameInConfig;
    }
}

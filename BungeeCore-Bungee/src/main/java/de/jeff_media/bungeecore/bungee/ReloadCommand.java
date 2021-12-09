package de.jeff_media.bungeecore.bungee;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("bungeecore|bcore")
@CommandPermission("bungeecore.reload")
public class ReloadCommand extends BaseCommand {

    private final BungeeCore main;

    public ReloadCommand(BungeeCore main) {
        this.main = main;
    }

    @Subcommand("reload")
    @CommandPermission("bungeecore.reload")
    public void reload(CommandSender sender) {
        main.reload();
        sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',"&aBungeeCORE reloaded.")));
    }


}

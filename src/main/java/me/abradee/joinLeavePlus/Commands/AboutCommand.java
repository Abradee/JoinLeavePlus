package me.abradee.joinLeavePlus.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AboutCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        String version = JavaPlugin.getProvidingPlugin(getClass()).getDescription().getVersion();

        Component message = Component.text(
                "===JoinLeavePlus===\n" +
                        "\tAbout Plugin:\n" +
                        "\tVersion: " + version + "\n" +
                        "\tA paper plugin that adds the ability to customize your join, leave, and first time join messages on your server using MiniMessage and legacy color codes!" +
                        "\tThe full list is at /help joinleaveplus.\n",
                NamedTextColor.YELLOW
        );

        if (sender instanceof Player p) {
            p.sendMessage(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(message);
        }

        return true;
    }
}
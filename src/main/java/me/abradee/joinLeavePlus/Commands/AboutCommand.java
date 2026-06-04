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
                        "\tFull description: This is a plugin that replaces the vanilla Join/Leave plugins to a configurable message." +
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
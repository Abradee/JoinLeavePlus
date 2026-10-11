package me.abradee.joinLeavePlus.Commands;

import org.bukkit.command.CommandSender;

public interface SubCommand {

    String getName();
    String getDescription();
    String getSyntax();
    String getPermission();

    void execute(CommandSender sender, String[] args);
}
/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.Commands;

import me.abradee.joinLeavePlus.Commands.SubCommands.ConfigCommand;
import me.abradee.joinLeavePlus.Commands.SubCommands.ReloadCommand;
import me.abradee.joinLeavePlus.GUI.ConfigGui;
import me.abradee.joinLeavePlus.JoinLeavePlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JoinLeavePlusCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public JoinLeavePlusCommand(JoinLeavePlus plugin, @Nullable ConfigGui configGui) {
        // registerSubCommand(new ConfigCommand(configGui));
        registerSubCommand(new ReloadCommand(plugin));
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Component.text("--- JoinLeavePlus Commands ---", NamedTextColor.GOLD));
            for (SubCommand sub : subCommands.values()) {
                if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                    sender.sendMessage(Component.text(sub.getSyntax() + " ", NamedTextColor.YELLOW)
                            .append(Component.text("- " + sub.getDescription(), NamedTextColor.GRAY)));
                }
            }
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand target = subCommands.get(subCommandName);

        if (target != null) {
            target.execute(sender, args);
        } else {
            sender.sendMessage(Component.text("Unknown subcommand. Use /" + label + " to see available commands.", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String currentInput = args[0].toLowerCase();

            for (SubCommand sub : subCommands.values()) {
                if (sub.getPermission() == null || sender.hasPermission(sub.getPermission())) {
                    if (sub.getName().toLowerCase().startsWith(currentInput)) {
                        completions.add(sub.getName());
                    }
                }
            }
            return completions;
        }

        return List.of();
    }
}
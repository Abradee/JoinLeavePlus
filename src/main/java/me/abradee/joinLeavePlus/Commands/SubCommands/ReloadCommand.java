/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
*/

package me.abradee.joinLeavePlus.Commands.SubCommands;

import me.abradee.joinLeavePlus.Commands.SubCommand;
import me.abradee.joinLeavePlus.JoinLeavePlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {

    private static final String PERMISSION = "joinleaveplus.reload";
    private final JoinLeavePlus plugin;

    public ReloadCommand(JoinLeavePlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin configuration files.";
    }

    @Override
    public String getSyntax() {
        return "/joinleaveplus reload";
    }

    @Override
    public String getPermission() {
        return PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Component.text("You do not have permission to reload the configuration.", NamedTextColor.RED));
            return;
        }

        try {
            plugin.reloadConfig();
            sender.sendMessage(Component.text("JoinLeavePlus configuration reloaded successfully!", NamedTextColor.GREEN));
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to reload configuration: " + e.getMessage());
            sender.sendMessage(Component.text("An error occurred while reloading the configuration. Check console.", NamedTextColor.RED));
        }
    }
}
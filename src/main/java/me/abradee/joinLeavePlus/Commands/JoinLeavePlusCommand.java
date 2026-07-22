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

import me.abradee.joinLeavePlus.GUI.ConfigGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JoinLeavePlusCommand implements CommandExecutor, TabCompleter {
    private static final String CONFIG_PERMISSION = "joinleaveplus.config";

    private final ConfigGui configGui;

    public JoinLeavePlusCommand(@Nullable ConfigGui configGui) {
        this.configGui = configGui;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("config")) {
            sender.sendMessage(Component.text("Usage: /" + label + " config", NamedTextColor.YELLOW));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("The configuration menu can only be opened in-game.", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission(CONFIG_PERMISSION)) {
            player.sendMessage(Component.text("You do not have permission to edit the configuration.", NamedTextColor.RED));
            return true;
        }

        if (configGui == null) {
            player.sendMessage(Component.text("ProtocolLib is required for the in-game configuration menu. ",
                            NamedTextColor.RED)
                    .append(Component.text("[Download ProtocolLib]", NamedTextColor.AQUA,
                                    TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl("https://github.com/dmulloy2/ProtocolLib/releases/latest"))
                            .hoverEvent(HoverEvent.showText(Component.text("Open the ProtocolLib download page",
                                    NamedTextColor.YELLOW)))));
            return true;
        }

        configGui.open(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                 @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(CONFIG_PERMISSION)
                && "config".startsWith(args[0].toLowerCase())) {
            return List.of("config");
        }

        return List.of();
    }
}

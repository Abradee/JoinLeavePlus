/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.Commands.SubCommands;

import me.abradee.joinLeavePlus.Commands.SubCommand;
import me.abradee.joinLeavePlus.GUI.ConfigGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ConfigCommand implements SubCommand {

    private static final String PERMISSION = "joinleaveplus.config";
    private final ConfigGui configGui;

    public ConfigCommand(@Nullable ConfigGui configGui) {
        this.configGui = configGui;
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "Opens the in-game configuration menu.";
    }

    @Override
    public String getSyntax() {
        return "/jlp config";
    }

    @Override
    public String getPermission() {
        return PERMISSION;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("The configuration menu can only be opened in-game.", NamedTextColor.RED));
            return;
        }

        if (!player.hasPermission(getPermission())) {
            player.sendMessage(Component.text("You do not have permission to edit the configuration.", NamedTextColor.RED));
            return;
        }

        if (configGui == null) {
            player.sendMessage(Component.text("ProtocolLib is required for the in-game configuration menu. ",
                            NamedTextColor.RED)
                    .append(Component.text("[Download ProtocolLib]", NamedTextColor.AQUA,
                                    TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl("https://github.com/dmulloy2/ProtocolLib/releases/latest"))
                            .hoverEvent(HoverEvent.showText(Component.text("Open the ProtocolLib download page",
                                    NamedTextColor.YELLOW)))));
            return;
        }

        configGui.open(player);
    }
}
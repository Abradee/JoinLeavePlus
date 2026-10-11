/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.Listeners.Leave;

import me.abradee.joinLeavePlus.Hooks.PAPIHook;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LeaveMessageListener {
    public static void handle(PlayerQuitEvent e, String name) {
        java.util.List<String> leaveMessages = JavaPlugin.getProvidingPlugin(LeaveMessageListener.class)
                .getConfig()
                .getStringList("leave");
        if (leaveMessages.isEmpty()) return;

        int leaveIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(leaveMessages.size());
        String msg = leaveMessages.get(leaveIndex).replace("%player%", name);

        boolean papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (msg.contains("<")) {
            if (papiEnabled) {
                msg = PAPIHook.parseToMiniMessage(e.getPlayer(), msg);
            }
            e.quitMessage(MiniMessage.miniMessage().deserialize(msg));
        } else {
            if (papiEnabled) {
                msg = PAPIHook.apply(e.getPlayer(), msg);
            }
            e.quitMessage(LegacyComponentSerializer.legacySection().deserialize(msg));
        }
    }
}
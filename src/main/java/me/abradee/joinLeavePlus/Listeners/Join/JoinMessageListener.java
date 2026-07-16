/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.Listeners.Join;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinMessageListener {
    public static void handle(PlayerJoinEvent e, String name) {
        java.util.List<String> joinMessages = JavaPlugin.getProvidingPlugin(JoinMessageListener.class).getConfig().getStringList("join");
        if (joinMessages.isEmpty()) return;

        int joinIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessages.size());
        String msg = joinMessages.get(joinIndex).replace("%player%", name);

        var joinMessageSerializer = msg.contains("<")
                ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

        e.joinMessage(joinMessageSerializer.deserialize(msg));
    }
}
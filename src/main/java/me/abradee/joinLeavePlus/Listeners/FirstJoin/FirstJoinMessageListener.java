/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.Listeners.FirstJoin;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FirstJoinMessageListener {
    public static void handle(PlayerJoinEvent e, String name) {
        java.util.List<String> firstJoinMessages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getStringList("first-time-join");
        if (firstJoinMessages.isEmpty()) return;

        int firstJoinIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstJoinMessages.size());
        String msg = firstJoinMessages.get(firstJoinIndex).replace("%player%", name);

        var firstJoinMessageSerializer = msg.contains("<")
                ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

        e.joinMessage(firstJoinMessageSerializer.deserialize(msg));
    }
}
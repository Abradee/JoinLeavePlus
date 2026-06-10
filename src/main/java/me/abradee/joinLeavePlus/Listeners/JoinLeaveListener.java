package me.abradee.joinLeavePlus.Listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import java.time.Duration;

public class JoinLeaveListener implements Listener {

    private final LegacyComponentSerializer serializer =
            LegacyComponentSerializer.legacyAmpersand();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();

        if (!e.getPlayer().hasPlayedBefore()) {
            java.util.List<String> firstMessages = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getStringList("first-time-join");
            java.util.List<String> firstMessagesTitle = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getStringList("first-time-join-title");
            java.util.List<String> firstMessagesSubtitle = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getStringList("first-time-join-subtitle");

            if (firstMessages.isEmpty()) return;

            int firstIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstMessages.size());
            String msg = firstMessages.get(firstIndex).replace("%player%", name);

            var serializer = msg.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            e.joinMessage(serializer.deserialize(msg));
        } else {
            java.util.List<String> joinMessages = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getStringList("join");
            java.util.List<String> joinMessagesTitle = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getStringList("join-title");
            java.util.List<String> joinMessagesSubtitle = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getStringList("join-subtitle");

            if (joinMessages.isEmpty()) return;

            int joinIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessages.size());
            String msg = joinMessages.get(joinIndex).replace("%player%", name);
            joinMessagesTitle = joinMessagesTitle.replace("%player%", name);

            var serializer = msg.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            e.joinMessage(serializer.deserialize(msg));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();

        java.util.List<String> leaveMessages = JavaPlugin.getProvidingPlugin(getClass())
                .getConfig().getStringList("leave");

        if (leaveMessages.isEmpty()) return;

        int leaveIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(leaveMessages.size());
        String msg = leaveMessages.get(leaveIndex).replace("%player%", name);

        var serializer = msg.contains("<")
                ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

        e.quitMessage(serializer.deserialize(msg));
    }
}

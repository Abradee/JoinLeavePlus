package me.abradee.joinLeavePlus.Listeners;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LeaveMessageListener {
    public static void handle(PlayerQuitEvent e, String name) {
        java.util.List<String> leaveMessages = JavaPlugin.getProvidingPlugin(LeaveMessageListener.class).getConfig().getStringList("leave");
        if (leaveMessages.isEmpty()) return;

        int leaveIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(leaveMessages.size());
        String msg = leaveMessages.get(leaveIndex).replace("%player%", name);

        var leaveMessageSerializer = msg.contains("<")
                ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

        e.quitMessage(leaveMessageSerializer.deserialize(msg));
    }
}
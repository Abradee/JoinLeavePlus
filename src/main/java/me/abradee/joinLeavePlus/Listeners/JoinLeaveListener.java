package me.abradee.joinLeavePlus.Listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinLeaveListener implements Listener {

    private final LegacyComponentSerializer serializer =
            LegacyComponentSerializer.legacyAmpersand();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();

        if (!e.getPlayer().hasPlayedBefore()) {
            String msg = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getString("first-time-join");
            e.joinMessage(serializer.deserialize(
                    msg.replace("%player%", name)
            ));
        } else {
            String msg = JavaPlugin.getProvidingPlugin(getClass())
                    .getConfig().getString("join");
            e.joinMessage(serializer.deserialize(
                    msg.replace("%player%", name)
            ));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();

        String msg = JavaPlugin.getProvidingPlugin(getClass())
                .getConfig().getString("leave");
        e.quitMessage(serializer.deserialize(
                msg.replace("%player%", name)
        ));
    }
}

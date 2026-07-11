package me.abradee.joinLeavePlus.Listeners;

import me.abradee.joinLeavePlus.Listeners.FirstJoin.FirstJoinMessageListener;
import me.abradee.joinLeavePlus.Listeners.FirstJoin.FirstJoinSoundListener;
import me.abradee.joinLeavePlus.Listeners.FirstJoin.FirstJoinTitleListener;
import me.abradee.joinLeavePlus.Listeners.FirstJoin.FirstJoinBookListener;
import me.abradee.joinLeavePlus.Listeners.Join.JoinMessageListener;
import me.abradee.joinLeavePlus.Listeners.Join.JoinSoundListener;
import me.abradee.joinLeavePlus.Listeners.Join.JoinTitleListener;
import me.abradee.joinLeavePlus.Listeners.Leave.LeaveMessageListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MainListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String useFirstJoinMessages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getString("first-time-join-messages");

        Player player = e.getPlayer();
        String name = player.getName();

        if (!player.hasPlayedBefore()) {
            FirstJoinMessageListener.handle(e, name);
            FirstJoinSoundListener.handle(player);
            FirstJoinTitleListener.handle(player, name);
            FirstJoinBookListener.handle(e, name);
        } else {
            JoinMessageListener.handle(e, name);
            JoinSoundListener.handle(player);
            JoinTitleListener.handle(player, name);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        LeaveMessageListener.handle(e, name);
    }
}
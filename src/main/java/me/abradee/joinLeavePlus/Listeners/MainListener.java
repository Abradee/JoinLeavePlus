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
        Boolean useFirstJoinMessages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("first-time-join-messages");
        Boolean useFirstJoinBooks = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("first-time-join-books");
        Boolean useFirstJoinSounds = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("first-time-join-sounds");
        Boolean useFirstJoinTitles = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("first-time-join-titles");
        Boolean useJoinMessages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("join-messages");
        Boolean useJoinTitles = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("join-titles");
        Boolean useJoinSounds = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("join-sounds");


        Player player = e.getPlayer();
        String name = player.getName();

        if (!player.hasPlayedBefore()) {
            if (useFirstJoinMessages = true ) {
                FirstJoinMessageListener.handle(e, name);
            }
            if (useFirstJoinSounds = true ) {
                FirstJoinSoundListener.handle(player);
            }
            if (useFirstJoinTitles = true ) {
                FirstJoinTitleListener.handle(player, name);
            }
            if (useFirstJoinBooks = true ) {
                FirstJoinBookListener.handle(e, name);
            }
        } else {
            if (useJoinMessages = true ) {
                JoinMessageListener.handle(e, name);
            }
            if (useJoinSounds = true ) {
                JoinSoundListener.handle(player);
            }
            if (useJoinTitles = true ) {
                JoinTitleListener.handle(player, name);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Boolean useLeaveMessages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getBoolean("leave-messages");
        Player player = e.getPlayer();
        String name = player.getName();

        if (useLeaveMessages = true ) {
            LeaveMessageListener.handle(e, name);
        }
    }
}
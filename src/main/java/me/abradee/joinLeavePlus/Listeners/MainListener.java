package me.abradee.joinLeavePlus.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        if (!player.hasPlayedBefore()) {
            // First time join checklist
            FirstJoinMessageListener.handle(e, name);
            FirstJoinSoundListener.handle(player);
            FirstJoinTitleListener.handle(player, name);
        } else {
            // Regular join checklist
            JoinMessageListener.handle(e, name);
            JoinSoundListener.handle(player);
            JoinTitleListener.handle(player, name);
        }
    }
}
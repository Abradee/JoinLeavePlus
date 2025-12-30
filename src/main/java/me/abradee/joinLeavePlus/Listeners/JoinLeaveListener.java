package me.abradee.joinLeavePlus.Listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();

        if (!e.getPlayer().hasPlayedBefore()) {
            e.joinMessage(Component.text("+ ", NamedTextColor.GOLD, TextDecoration.BOLD).append(Component.text(name, NamedTextColor.AQUA, TextDecoration.BOLD)).append(Component.text(" joined for the first time!", NamedTextColor.YELLOW)));
        } else {
            e.joinMessage(Component.text("+ ", NamedTextColor.GREEN, TextDecoration.BOLD).append(Component.text(name, NamedTextColor.GREEN)).append(Component.text(" has joined.", NamedTextColor.GRAY)));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();

        e.quitMessage(Component.text("- ", NamedTextColor.RED, TextDecoration.BOLD).append(Component.text(name, NamedTextColor.RED)).append(Component.text(" has left.", NamedTextColor.GRAY)));
    }
}

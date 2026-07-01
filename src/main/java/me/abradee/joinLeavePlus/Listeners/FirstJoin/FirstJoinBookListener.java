package me.abradee.joinLeavePlus.Listeners.FirstJoin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinBookListener {
    public static void handle(PlayerJoinEvent e, String name) {
        java.util.List<String> firstJoinBookTitle = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getStringList("book-title");
        java.util.List<String> firstJoinBookAuthor = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getStringList("book-author");
        java.util.List<String> firstJoinBookPages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getStringList("book-pages");


    }
}

package me.abradee.joinLeavePlus.Listeners.FirstJoin;

import net.kyori.adventure.title.Title;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.inventory.Book;

public class FirstJoinBookListener {
    public static void handle(PlayerJoinEvent e, String name) {
        String firstJoinBookTitle = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getString("book-title");
        String firstJoinBookAuthor = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getString("book-author");
        java.util.List<String> firstJoinBookPages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getStringList("book-pages");

        Book.Builder(firstJoinBookTitle Title, firstJoinBookAuthor Author);
    }
}
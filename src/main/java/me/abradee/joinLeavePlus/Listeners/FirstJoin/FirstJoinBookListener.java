package me.abradee.joinLeavePlus.Listeners.FirstJoin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.inventory.Book;

public class FirstJoinBookListener {
    public static void handle(PlayerJoinEvent e, String name) {
        String firstJoinBookTitle = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getString("book-title", "Welcome!");
        String firstJoinBookAuthor = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getString("book-author", "Staff");
        java.util.List<String> firstJoinBookPages = JavaPlugin.getProvidingPlugin(FirstJoinMessageListener.class).getConfig().getStringList("book-pages");

        Book.Builder bookBuilder = Book.builder()
                .title(parseText(firstJoinBookTitle))
                .author(parseText(firstJoinBookAuthor));

        for (String pageText : firstJoinBookPages) {
            bookBuilder.addPage(parseText(pageText));
        }

        Book firstJoinBook = bookBuilder.build();

        e.getPlayer().openBook(firstJoinBook);
    }

    private static Component parseText(String msg) {
        if (msg == null) return Component.empty();

        return msg.contains("<")
                ? MiniMessage.miniMessage().deserialize(msg)
                : LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
    }
}
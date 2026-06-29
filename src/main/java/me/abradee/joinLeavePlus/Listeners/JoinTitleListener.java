package me.abradee.joinLeavePlus.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import java.time.Duration;

public class JoinTitleListener {
    public static void handle(Player player, String name) {
        java.util.List<String> joinMessagesTitleList = JavaPlugin.getProvidingPlugin(JoinTitleListener.class).getConfig().getStringList("join-title");
        java.util.List<String> joinMessagesSubtitleList = JavaPlugin.getProvidingPlugin(JoinTitleListener.class).getConfig().getStringList("join-subtitle");

        int titleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessagesTitleList.size());
        String singleTitleMessage = joinMessagesTitleList.get(titleIndex).replace("%player%", name);
        int subtitleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessagesSubtitleList.size());
        String singleSubtitleMessage = joinMessagesSubtitleList.get(subtitleIndex).replace("%player%", name);

        if (!singleTitleMessage.isEmpty() || !singleSubtitleMessage.isEmpty()) {
            var titleMessageSerializer = singleTitleMessage.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            var subtitleMessageSerializer = singleSubtitleMessage.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            Component mainTitle = singleTitleMessage.isEmpty() ? Component.empty() : titleMessageSerializer.deserialize(singleTitleMessage);
            Component subTitle = singleSubtitleMessage.isEmpty() ? Component.empty() : subtitleMessageSerializer.deserialize(singleSubtitleMessage);

            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000));
            Title fullTitle = Title.title(mainTitle, subTitle, times);

            player.showTitle(fullTitle);
        }
    }
}
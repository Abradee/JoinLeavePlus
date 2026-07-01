package me.abradee.joinLeavePlus.Listeners.FirstJoin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import java.time.Duration;

public class FirstJoinTitleListener {
    public static void handle(Player player, String name) {
        java.util.List<String> firstJoinTitleList = JavaPlugin.getProvidingPlugin(FirstJoinTitleListener.class).getConfig().getStringList("first-time-join-title");
        java.util.List<String> firstJoinSubtitleList = JavaPlugin.getProvidingPlugin(FirstJoinTitleListener.class).getConfig().getStringList("first-time-join-subtitle");

        int firstJoinTitleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstJoinTitleList.size());
        String singleFirstJoinTitleMessage = firstJoinTitleList.get(firstJoinTitleIndex).replace("%player%", name);

        int firstJoinSubtitleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstJoinSubtitleList.size());
        String singleFirstJoinSubtitleMessage = firstJoinSubtitleList.get(firstJoinSubtitleIndex).replace("%player%", name);

        if (!singleFirstJoinTitleMessage.isEmpty() || !singleFirstJoinSubtitleMessage.isEmpty()) {
            var firstJoinTitleSerializer = singleFirstJoinTitleMessage.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            var firstJoinSubtitleSerializer = singleFirstJoinSubtitleMessage.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            Component firstJoinMainTitle = singleFirstJoinTitleMessage.isEmpty() ? Component.empty() : firstJoinTitleSerializer.deserialize(singleFirstJoinTitleMessage);
            Component firstJoinSubTitle = singleFirstJoinSubtitleMessage.isEmpty() ? Component.empty() : firstJoinSubtitleSerializer.deserialize(singleFirstJoinSubtitleMessage);

            Title.Times firstJoinTimes = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000));
            Title firstJoinFullTitle = Title.title(firstJoinMainTitle, firstJoinSubTitle, firstJoinTimes);

            player.showTitle(firstJoinFullTitle);
        }
    }
}
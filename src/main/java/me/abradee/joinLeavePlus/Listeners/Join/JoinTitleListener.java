/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.Listeners.Join;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import java.time.Duration;

public class JoinTitleListener {
    public static void handle(Player player, String name) {
        java.util.List<String> joinMessagesTitleList = JavaPlugin.getProvidingPlugin(JoinTitleListener.class).getConfig().getStringList("join-title");
        java.util.List<String> joinMessagesSubtitleList = JavaPlugin.getProvidingPlugin(JoinTitleListener.class).getConfig().getStringList("join-subtitle");

        String singleTitleMessage = randomMessage(joinMessagesTitleList).replace("%player%", name);
        String singleSubtitleMessage = randomMessage(joinMessagesSubtitleList).replace("%player%", name);

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

    private static String randomMessage(java.util.List<String> messages) {
        if (messages.isEmpty()) return "";

        int index = java.util.concurrent.ThreadLocalRandom.current().nextInt(messages.size());
        return messages.get(index);
    }
}

package me.abradee.joinLeavePlus.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import java.time.Duration;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.key.Key;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();
        org.bukkit.entity.Player player = e.getPlayer();

        if (!player.hasPlayedBefore()) {
            String firstSoundKey = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getString("first-join-sound", "minecraft:ui.toast.challenge_complete");
            Sound firstJoinSound = Sound.sound(Key.key(firstSoundKey), Sound.Source.PLAYER, 1f, 1f);

            java.util.List<String> firstMessages = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getStringList("first-time-join");
            java.util.List<String> firstMessagesTitleList = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getStringList("first-time-join-title");
            java.util.List<String> firstMessagesSubtitleList = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getStringList("first-time-join-subtitle");

            if (firstMessages.isEmpty()) return;

            int firstIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstMessages.size());
            String msg = firstMessages.get(firstIndex).replace("%player%", name);

            int firstTitleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstMessagesTitleList.size());
            String singleFirstTitleMessage = firstMessagesTitleList.get(firstTitleIndex).replace("%player%", name);
            int firstSubtitleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(firstMessagesSubtitleList.size());
            String singleFirstSubtitleMessage = firstMessagesSubtitleList.get(firstSubtitleIndex).replace("%player%", name);

            var firstJoinMessageSerializer = msg.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            e.joinMessage(firstJoinMessageSerializer.deserialize(msg));

            player.playSound(firstJoinSound);

            if (!singleFirstTitleMessage.isEmpty() || !singleFirstSubtitleMessage.isEmpty()) {
                var firstTitleMessageSerializer = singleFirstTitleMessage.contains("<")
                        ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                        : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

                var firstSubtitleMessageSerializer = singleFirstSubtitleMessage.contains("<")
                        ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                        : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

                Component firstMainTitle = singleFirstTitleMessage.isEmpty() ? Component.empty() : firstTitleMessageSerializer.deserialize(singleFirstTitleMessage);
                Component firstSubTitle = singleFirstSubtitleMessage.isEmpty() ? Component.empty() : firstSubtitleMessageSerializer.deserialize(singleFirstSubtitleMessage);

                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofMillis(1000));
                Title fullTitle = Title.title(firstMainTitle, firstSubTitle, times);

                player.showTitle(fullTitle);
            }
        } else {
            String joinSoundKey = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getString("join-sound", "minecraft:entity.experience_orb.pickup");
            Sound joinSound = Sound.sound(Key.key(joinSoundKey), Sound.Source.PLAYER, 1f, 1f);

            java.util.List<String> joinMessages = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getStringList("join");
            java.util.List<String> joinMessagesTitleList = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getStringList("join-title");
            java.util.List<String> joinMessagesSubtitleList = JavaPlugin.getProvidingPlugin(getClass()).getConfig().getStringList("join-subtitle");

            if (joinMessages.isEmpty()) return;

            int joinIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessages.size());
            String msg = joinMessages.get(joinIndex).replace("%player%", name);

            int titleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessagesTitleList.size());
            String singleTitleMessage = joinMessagesTitleList.get(titleIndex).replace("%player%", name);
            int subtitleIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(joinMessagesSubtitleList.size());
            String singleSubtitleMessage = joinMessagesSubtitleList.get(subtitleIndex).replace("%player%", name);

            var joinMessageSerializer = msg.contains("<")
                    ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

            e.joinMessage(joinMessageSerializer.deserialize(msg));

            player.playSound(joinSound);

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

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();

        java.util.List<String> leaveMessages = JavaPlugin.getProvidingPlugin(getClass())
                .getConfig().getStringList("leave");

        if (leaveMessages.isEmpty()) return;

        int leaveIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(leaveMessages.size());
        String msg = leaveMessages.get(leaveIndex).replace("%player%", name);

        var serializer = msg.contains("<")
                ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                : net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand();

        e.quitMessage(serializer.deserialize(msg));
    }
}
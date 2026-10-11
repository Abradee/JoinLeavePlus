package me.abradee.joinLeavePlus.Hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PAPIHook {
    private static final Pattern PAPI_PATTERN = Pattern.compile("[%]([^%]+)[%]");

    // Serializer that parses section signs (§) AND hex colors
    private static final LegacyComponentSerializer SECTION_SERIALIZER =
            LegacyComponentSerializer.builder().character('§').hexColors().build();

    private PAPIHook() {}

    public static String apply(Player player, String message) {
        String parsed = PlaceholderAPI.setPlaceholders(player, message);
        return parsed.replace('&', '§');
    }

    public static String parseToMiniMessage(Player player, String message) {
        Matcher matcher = PAPI_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String rawPlaceholder = matcher.group(0);
            String parsed = PlaceholderAPI.setPlaceholders(player, rawPlaceholder);
            String normalized = parsed.replace('&', '§');
            Component comp = SECTION_SERIALIZER.deserialize(normalized);
            String miniMessageEquivalent = MiniMessage.miniMessage().serialize(comp);

            matcher.appendReplacement(sb, Matcher.quoteReplacement(miniMessageEquivalent));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
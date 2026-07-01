package me.abradee.joinLeavePlus.Listeners.Join;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.key.Key;

public class JoinSoundListener {
    public static void handle(Player player) {
        String joinSoundKey = JavaPlugin.getProvidingPlugin(JoinSoundListener.class).getConfig().getString("join-sound", "minecraft:entity.experience_orb.pickup");
        Sound joinSound = Sound.sound(Key.key(joinSoundKey), Sound.Source.PLAYER, 1f, 1f);
        player.playSound(joinSound);
    }
}
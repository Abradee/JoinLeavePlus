package me.abradee.joinLeavePlus.Listeners.FirstJoin;

import me.abradee.joinLeavePlus.Listeners.Join.JoinSoundListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.key.Key;

public class FirstJoinSoundListener {
    public static void handle(Player player) {
        String firstJoinSoundKey = JavaPlugin.getProvidingPlugin(JoinSoundListener.class).getConfig().getString("first-timejoin-sound", "minecraft:entity.experience_orb.pickup");
        Sound firstJoinSound = Sound.sound(Key.key(firstJoinSoundKey), Sound.Source.PLAYER, 1f, 1f);
        player.playSound(firstJoinSound);
    }
}
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
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.key.Key;

public class JoinSoundListener {
    public static void handle(Player player) {
        String joinSoundKey = JavaPlugin.getProvidingPlugin(JoinSoundListener.class).getConfig().getString("join-sound", "minecraft:entity.experience_orb.pickup");
        Sound joinSound = Sound.sound(Key.key(joinSoundKey), Sound.Source.PLAYER, 1f, 1f);
        player.playSound(joinSound);
    }
}
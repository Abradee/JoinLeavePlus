package me.abradee.joinLeavePlus;

import me.abradee.joinLeavePlus.Listeners.JoinLeaveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinLeavePlus extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("The plugin is starting...");
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        getLogger().info("The plugin has started.");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has stopped.");
    }
}

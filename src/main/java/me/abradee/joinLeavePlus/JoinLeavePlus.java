package me.abradee.joinLeavePlus;

import me.abradee.joinLeavePlus.Listeners.JoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinLeavePlus extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(("The plugin is starting..."));
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        getLogger().info("The plugin has started.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("The plugin has stopped.");
    }
}

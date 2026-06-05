package me.abradee.joinLeavePlus;

import me.abradee.joinLeavePlus.Listeners.JoinLeaveListener;
import me.abradee.joinLeavePlus.Commands.AboutCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinLeavePlus extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("JoinLeavePlus is initializing...");
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        getCommand("about-joinleaveplus").setExecutor(new AboutCommand());
        getLogger().info("The plugin has started.\nFeel free to donate through https://patreon.com/abradee");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has stopped.\nThanks for using JoinLeavePlus!");
    }
}

package me.abradee.joinLeavePlus;

import me.abradee.joinLeavePlus.Listeners.*;
import me.abradee.joinLeavePlus.Commands.AboutCommand;
import org.bukkit.plugin.java.JavaPlugin;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;

// includes other code from clickism's ModrinthUpdateChecker
// check LICENSE or check on GitHub

public final class JoinLeavePlus extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("JoinLeavePlus is initializing...");
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        getCommand("about-joinleaveplus").setExecutor(new AboutCommand());
        getLogger().info("The plugin has started.");
        getLogger().info("Feel free to donate through https://patreon.com/abradee");
        new ModrinthUpdateChecker("joinleaveplus", "paper", null)
                .checkVersion(latestVersion -> {
                    String currentVersion = getDescription().getVersion();

                    if (currentVersion.contains("-BUILD")) {
                        getLogger().warning("Running a development build (" + currentVersion + "). Skipping update check.");
                        return;
                    }

                    if (currentVersion.equals(latestVersion)) { // later change this to just check if the version is lower or higher than the posted version due to unreleased versions saying they're old.
                        getLogger().info("You are running the latest version!");
                    } else {
                        getLogger().warning("A new update is available: v" + latestVersion);
                        getLogger().warning("Your current version: v" + currentVersion);
                        getLogger().warning("Download it here: https://modrinth.com/plugin/joinleaveplus");
                    }
                });
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has stopped.");
        getLogger().info("Thanks for using JoinLeavePlus!");
    }
}

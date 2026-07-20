/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus;

import me.abradee.joinLeavePlus.Listeners.*;
import me.abradee.joinLeavePlus.Commands.AboutCommand;
import me.abradee.joinLeavePlus.Commands.JoinLeavePlusCommand;
import me.abradee.joinLeavePlus.GUI.ConfigGui;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;

import java.util.Objects;


public final class JoinLeavePlus extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("JoinLeavePlus is initializing...");
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        getCommand("about-joinleaveplus").setExecutor(new AboutCommand());

        ConfigGui configGui = new ConfigGui(this);
        getServer().getPluginManager().registerEvents(configGui, this);
        JoinLeavePlusCommand joinLeavePlusCommand = new JoinLeavePlusCommand(configGui);
        PluginCommand command = Objects.requireNonNull(getCommand("joinleaveplus"), "joinleaveplus command is not registered");
        command.setExecutor(joinLeavePlusCommand);
        command.setTabCompleter(joinLeavePlusCommand);

        getLogger().info("The plugin has started.");
        getLogger().info("Feel free to donate through https://patreon.com/abradee");
        new ModrinthUpdateChecker("joinleaveplus", "paper", null)
                .checkVersion(latestVersion -> {
                    String currentVersion = getDescription().getVersion();

                    if (currentVersion.contains("-BUILD") || currentVersion.contains("-SNAPSHOT")) {
                        getLogger().warning("Running a development build (" + currentVersion + "). Skipping update check.");
                        getLogger().warning("Development builds are NOT for anything outside of testing.");
                        getLogger().warning("See https://abradee.github.io/JoinLeavePlus/docs/development-build.html for more info.");
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

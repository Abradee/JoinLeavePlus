/*
    (c) 2026 JoinLeavePlus under the GPLv3 License
    (c) 2026 Abradee
    Check LICENSE for more license information.
    Check CONTRIBUTING.md for contributing information.
    =========================================================
    includes other code from clickism's ModrinthUpdateChecker
    check LICENSE or check on GitHub
*/

package me.abradee.joinLeavePlus.GUI;

import me.abradee.joinLeavePlus.JoinLeavePlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConfigGui implements Listener {
    private static final int MENU_SIZE = 27;
    private static final int BACK_SLOT = 22;
    private static final int ANVIL_RESULT_SLOT = 2;

    private static final Map<Integer, MenuType> MAIN_MENU_ENTRIES = Map.of(
            11, MenuType.TOGGLES,
            12, MenuType.MESSAGES,
            13, MenuType.TITLES,
            14, MenuType.SOUNDS,
            15, MenuType.BOOK
    );

    private static final Map<MenuType, List<MenuSetting>> MENU_SETTINGS = Map.of(
            MenuType.TOGGLES, List.of(
                    setting(9, "first-time-join-messages", "First Join Messages", Material.LIME_DYE, ValueType.BOOLEAN),
                    setting(10, "join-messages", "Join Messages", Material.LIME_DYE, ValueType.BOOLEAN),
                    setting(11, "leave-messages", "Leave Messages", Material.RED_DYE, ValueType.BOOLEAN),
                    setting(12, "first-time-join-sounds", "First Join Sounds", Material.NOTE_BLOCK, ValueType.BOOLEAN),
                    setting(13, "join-sounds", "Join Sounds", Material.NOTE_BLOCK, ValueType.BOOLEAN),
                    setting(14, "first-time-join-books", "First Join Books", Material.WRITTEN_BOOK, ValueType.BOOLEAN),
                    setting(15, "first-time-join-titles", "First Join Titles", Material.NAME_TAG, ValueType.BOOLEAN),
                    setting(16, "join-titles", "Join Titles", Material.NAME_TAG, ValueType.BOOLEAN)
            ),
            MenuType.MESSAGES, List.of(
                    setting(11, "first-time-join", "First Join Messages", Material.OAK_SIGN, ValueType.STRING_LIST),
                    setting(13, "join", "Join Messages", Material.LIME_DYE, ValueType.STRING_LIST),
                    setting(15, "leave", "Leave Messages", Material.RED_DYE, ValueType.STRING_LIST)
            ),
            MenuType.TITLES, List.of(
                    setting(10, "first-time-join-title", "First Join Titles", Material.NAME_TAG, ValueType.STRING_LIST),
                    setting(11, "first-time-join-subtitle", "First Join Subtitles", Material.PAPER, ValueType.STRING_LIST),
                    setting(15, "join-title", "Join Titles", Material.NAME_TAG, ValueType.STRING_LIST),
                    setting(16, "join-subtitle", "Join Subtitles", Material.PAPER, ValueType.STRING_LIST)
            ),
            MenuType.SOUNDS, List.of(
                    setting(11, "first-join-sound", "First Join Sound", Material.JUKEBOX, ValueType.SOUND),
                    setting(15, "join-sound", "Join Sound", Material.NOTE_BLOCK, ValueType.SOUND)
            ),
            MenuType.BOOK, List.of(
                    setting(11, "book-title", "Book Title", Material.NAME_TAG, ValueType.STRING),
                    setting(13, "book-author", "Book Author", Material.PLAYER_HEAD, ValueType.STRING),
                    setting(15, "book-pages", "Book Pages", Material.WRITABLE_BOOK, ValueType.STRING_LIST)
            )
    );

    private final JoinLeavePlus plugin;
    private final VirtualBookEditor bookEditor;
    private final Set<UUID> completedAnvilEdits = new HashSet<>();
    private boolean shuttingDown;

    public ConfigGui(JoinLeavePlus plugin) {
        this.plugin = plugin;
        this.bookEditor = new VirtualBookEditor(plugin);
    }

    public void open(Player player) {
        bookEditor.cancel(player.getUniqueId());
        openMenu(player, MenuType.MAIN);
    }

    public void shutdown() {
        shuttingDown = true;
        bookEditor.shutdown();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ConfigInventoryHolder holder)) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (holder.menuType == MenuType.ANVIL_EDITOR) {
            if (event.getRawSlot() == ANVIL_RESULT_SLOT && event.getView() instanceof AnvilView anvilView) {
                saveAnvilInput(player, holder.pendingInput, anvilView);
            }
            return;
        }

        if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
            return;
        }

        int slot = event.getRawSlot();
        if (holder.menuType == MenuType.MAIN) {
            MenuType selectedMenu = MAIN_MENU_ENTRIES.get(slot);
            if (selectedMenu != null) {
                openMenu(player, selectedMenu);
            } else if (slot == BACK_SLOT) {
                player.closeInventory();
            }
            return;
        }

        if (slot == BACK_SLOT) {
            openMenu(player, MenuType.MAIN);
            return;
        }

        MENU_SETTINGS.get(holder.menuType).stream()
                .filter(menuSetting -> menuSetting.slot == slot)
                .map(MenuSetting::setting)
                .findFirst()
                .ifPresent(setting -> handleSettingClick(player, setting, holder.menuType));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ConfigInventoryHolder)) {
            return;
        }

        int topInventorySize = event.getView().getTopInventory().getSize();
        if (event.getRawSlots().stream().anyMatch(slot -> slot < topInventorySize)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfigInventoryHolder holder)
                || holder.menuType != MenuType.ANVIL_EDITOR) {
            return;
        }

        String value = event.getView().getRenameText();
        if (value == null) {
            value = "";
        }

        Setting setting = holder.pendingInput.setting;
        boolean valid = setting.valueType != ValueType.SOUND || soundExists(value.trim());
        List<Component> lore = new ArrayList<>();
        if (valid) {
            lore.add(line("Click to save this value.", NamedTextColor.YELLOW));
            if (setting.valueType == ValueType.STRING && !value.isEmpty()) {
                lore.add(Component.empty());
                lore.add(renderConfigText(value, (Player) event.getView().getPlayer()));
            }
        } else {
            lore.add(line("Enter a valid Minecraft sound key.", NamedTextColor.RED));
        }

        event.getView().setRepairCost(0);
        event.setResult(item(valid ? Material.LIME_DYE : Material.BARRIER,
                valid ? "Save" : "Invalid Sound", lore));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfigInventoryHolder holder)
                || holder.menuType != MenuType.ANVIL_EDITOR
                || shuttingDown) {
            return;
        }

        Player player = (Player) event.getPlayer();
        if (completedAnvilEdits.remove(player.getUniqueId())) {
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && !shuttingDown) {
                openMenu(player, holder.pendingInput.returnMenu);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        completedAnvilEdits.remove(playerId);
        bookEditor.cancel(playerId);
    }

    private void openMenu(Player player, MenuType menuType) {
        ConfigInventoryHolder holder = new ConfigInventoryHolder(menuType);
        Inventory inventory = holder.getInventory();
        fillBackground(inventory);

        if (menuType == MenuType.MAIN) {
            inventory.setItem(11, navigationItem(Material.LEVER, "Feature Toggles", "Enable or disable plugin features."));
            inventory.setItem(12, navigationItem(Material.OAK_SIGN, "Chat Messages", "Edit join and leave message lists."));
            inventory.setItem(13, navigationItem(Material.NAME_TAG, "Titles", "Edit join titles and subtitles."));
            inventory.setItem(14, navigationItem(Material.JUKEBOX, "Sounds", "Choose first-join and join sounds."));
            inventory.setItem(15, navigationItem(Material.WRITTEN_BOOK, "First Join Book", "Edit the welcome book contents."));
            inventory.setItem(BACK_SLOT, navigationItem(Material.BARRIER, "Close", "Close this menu."));
        } else {
            for (MenuSetting menuSetting : MENU_SETTINGS.get(menuType)) {
                inventory.setItem(menuSetting.slot, settingItem(player, menuSetting.setting));
            }
            inventory.setItem(BACK_SLOT, navigationItem(Material.ARROW, "Back", "Return to the main menu."));
        }

        player.openInventory(inventory);
    }

    private void handleSettingClick(Player player, Setting setting, MenuType returnMenu) {
        if (setting.valueType == ValueType.BOOLEAN) {
            boolean newValue = !plugin.getConfig().getBoolean(setting.path);
            plugin.getConfig().set(setting.path, newValue);
            plugin.saveConfig();
            player.sendMessage(Component.text(setting.displayName + " " + (newValue ? "enabled." : "disabled."),
                    newValue ? NamedTextColor.GREEN : NamedTextColor.RED));
            openMenu(player, returnMenu);
        } else if (setting.valueType == ValueType.STRING_LIST) {
            openBookEditor(player, new PendingInput(setting, returnMenu));
        } else {
            openAnvilEditor(player, new PendingInput(setting, returnMenu));
        }
    }

    private void openBookEditor(Player player, PendingInput pendingInput) {
        List<String> values = plugin.getConfig().getStringList(pendingInput.setting.path);
        if (values.size() > VirtualBookEditor.MAX_PAGES) {
            player.sendMessage(Component.text("This list has more than " + VirtualBookEditor.MAX_PAGES
                    + " entries and cannot be edited safely in a book.", NamedTextColor.RED));
            return;
        }

        player.closeInventory();
        player.sendMessage(Component.text("Each book page is one " + pendingInput.setting.displayName + " entry.",
                NamedTextColor.GRAY));
        player.sendMessage(Component.text("Right-click the air once to open the client-side Book & Quill.",
                NamedTextColor.AQUA));
        player.sendMessage(Component.text("Done saves. Finalizing with Sign cancels without signing a real book.",
                NamedTextColor.YELLOW));
        bookEditor.open(player, values, result -> finishBookEdit(player, pendingInput, result));
    }

    private void finishBookEdit(Player player, PendingInput pendingInput, VirtualBookEditor.EditResult result) {
        if (result.signed()) {
            player.sendMessage(Component.text("No changes were saved.", NamedTextColor.YELLOW));
        } else {
            plugin.getConfig().set(pendingInput.setting.path, result.pages());
            plugin.saveConfig();
            player.sendMessage(Component.text(pendingInput.setting.displayName + " saved.", NamedTextColor.GREEN));
        }
        openMenu(player, pendingInput.returnMenu);
    }

    private void openAnvilEditor(Player player, PendingInput pendingInput) {
        ConfigInventoryHolder holder = new ConfigInventoryHolder(pendingInput);
        String currentValue = plugin.getConfig().getString(pendingInput.setting.path, "");
        holder.getInventory().setItem(0, renameInputItem(pendingInput.setting.material, currentValue));
        player.openInventory(holder.getInventory());
    }

    private void saveAnvilInput(Player player, PendingInput pendingInput, AnvilView anvilView) {
        String value = anvilView.getRenameText();
        if (value == null) {
            value = "";
        }
        value = value.trim();

        if (pendingInput.setting.valueType == ValueType.SOUND) {
            NamespacedKey soundKey = NamespacedKey.fromString(value);
            if (soundKey == null || Registry.SOUNDS.get(soundKey) == null) {
                player.sendMessage(Component.text("Enter a valid Minecraft sound key.", NamedTextColor.RED));
                return;
            }
            value = soundKey.toString();
        }

        plugin.getConfig().set(pendingInput.setting.path, value);
        plugin.saveConfig();
        player.sendMessage(Component.text(pendingInput.setting.displayName + " saved.", NamedTextColor.GREEN));
        completedAnvilEdits.add(player.getUniqueId());
        openMenu(player, pendingInput.returnMenu);
    }

    private ItemStack settingItem(Player player, Setting setting) {
        List<Component> lore = new ArrayList<>();
        if (setting.valueType == ValueType.BOOLEAN) {
            boolean enabled = plugin.getConfig().getBoolean(setting.path);
            lore.add(line(enabled ? "Enabled" : "Disabled", enabled ? NamedTextColor.GREEN : NamedTextColor.RED));
            lore.add(Component.empty());
            lore.add(line("Click to toggle.", NamedTextColor.YELLOW));
        } else if (setting.valueType == ValueType.STRING_LIST) {
            List<String> values = plugin.getConfig().getStringList(setting.path);
            lore.add(line(values.size() + (values.size() == 1 ? " entry" : " entries"), NamedTextColor.AQUA));
            values.stream().limit(2).forEach(value -> lore.add(
                    Component.text("• ", NamedTextColor.GRAY).append(renderConfigText(value, player))));
            if (values.size() > 2) {
                lore.add(line("• ...", NamedTextColor.GRAY));
            }
            lore.add(Component.empty());
            lore.add(line("Click to edit in a virtual book.", NamedTextColor.YELLOW));
        } else {
            String value = plugin.getConfig().getString(setting.path, "");
            lore.add(value.isEmpty()
                    ? line("<empty>", NamedTextColor.GRAY)
                    : setting.valueType == ValueType.SOUND
                    ? line(abbreviate(value), NamedTextColor.AQUA)
                    : renderConfigText(value, player));
            lore.add(Component.empty());
            lore.add(line("Click to edit in an anvil.", NamedTextColor.YELLOW));
        }

        return item(setting.material, setting.displayName, lore);
    }

    private static ItemStack renameInputItem(Material material, String value) {
        ItemStack input = new ItemStack(material);
        ItemMeta meta = input.getItemMeta();
        meta.displayName(Component.text(value).decoration(TextDecoration.ITALIC, false));
        input.setItemMeta(meta);
        return input;
    }

    private static Component renderConfigText(String value, Player player) {
        if (value.isEmpty()) {
            return line("<empty>", NamedTextColor.GRAY);
        }

        String preview = value.replace("%player%", player.getName());
        try {
            Component rendered = preview.contains("<")
                    ? MiniMessage.miniMessage().deserialize(preview)
                    : LegacyComponentSerializer.legacyAmpersand().deserialize(preview);
            return rendered.decoration(TextDecoration.ITALIC, false);
        } catch (RuntimeException exception) {
            return line(abbreviate(value), NamedTextColor.GRAY);
        }
    }

    private static boolean soundExists(String value) {
        NamespacedKey soundKey = NamespacedKey.fromString(value);
        return soundKey != null && Registry.SOUNDS.get(soundKey) != null;
    }

    private static ItemStack navigationItem(Material material, String name, String description) {
        return item(material, name, List.of(
                line(description, NamedTextColor.GRAY),
                Component.empty(),
                line("Click to open.", NamedTextColor.YELLOW)
        ));
    }

    private static ItemStack item(Material material, String name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(line(name, NamedTextColor.GOLD));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static Component line(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    private static void fillBackground(Inventory inventory) {
        ItemStack background = item(Material.GRAY_STAINED_GLASS_PANE, " ", List.of());
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, background);
        }
    }

    private static String abbreviate(String value) {
        String singleLine = value.replace('\n', ' ');
        return singleLine.length() <= 55 ? singleLine : singleLine.substring(0, 52) + "...";
    }

    private static MenuSetting setting(int slot, String path, String displayName, Material material, ValueType valueType) {
        return new MenuSetting(slot, new Setting(path, displayName, material, valueType));
    }

    private enum MenuType {
        MAIN("JoinLeavePlus Config"),
        TOGGLES("Feature Toggles"),
        MESSAGES("Chat Messages"),
        TITLES("Titles"),
        SOUNDS("Sounds"),
        BOOK("First Join Book"),
        ANVIL_EDITOR("Edit Value");

        private final String title;

        MenuType(String title) {
            this.title = title;
        }
    }

    private enum ValueType {
        BOOLEAN,
        STRING,
        STRING_LIST,
        SOUND
    }

    private record Setting(String path, String displayName, Material material, ValueType valueType) {
    }

    private record MenuSetting(int slot, Setting setting) {
    }

    private record PendingInput(Setting setting, MenuType returnMenu) {
    }

    private static final class ConfigInventoryHolder implements InventoryHolder {
        private final MenuType menuType;
        private final PendingInput pendingInput;
        private final Inventory inventory;

        private ConfigInventoryHolder(MenuType menuType) {
            this.menuType = menuType;
            this.pendingInput = null;
            this.inventory = Bukkit.createInventory(this, MENU_SIZE, Component.text(menuType.title));
        }

        private ConfigInventoryHolder(PendingInput pendingInput) {
            this.menuType = MenuType.ANVIL_EDITOR;
            this.pendingInput = pendingInput;
            this.inventory = Bukkit.createInventory(this, InventoryType.ANVIL,
                    Component.text(pendingInput.setting.displayName));
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}

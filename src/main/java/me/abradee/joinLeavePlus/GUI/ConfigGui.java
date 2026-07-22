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

import io.papermc.paper.event.player.AsyncChatEvent;
import me.abradee.joinLeavePlus.JoinLeavePlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGui implements Listener {
    private static final int MENU_SIZE = 27;
    private static final int BACK_SLOT = 22;
    private static final int LIST_MENU_SIZE = 54;
    private static final int LIST_PAGE_SIZE = 45;
    private static final int LIST_PREVIOUS_SLOT = 45;
    private static final int LIST_ADD_SLOT = 47;
    private static final int LIST_BACK_SLOT = 49;
    private static final int LIST_CLEAR_SLOT = 51;
    private static final int LIST_NEXT_SLOT = 53;
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

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
    private final Map<UUID, PendingInput> pendingInputs = new ConcurrentHashMap<>();

    public ConfigGui(JoinLeavePlus plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        pendingInputs.remove(player.getUniqueId());
        openMenu(player, MenuType.MAIN);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ConfigInventoryHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)
                || event.getRawSlot() < 0
                || event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
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

        if (holder.menuType == MenuType.LIST_EDITOR) {
            handleListEditorClick(player, event, holder);
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
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        PendingInput pendingInput = pendingInputs.remove(player.getUniqueId());
        if (pendingInput == null) {
            return;
        }

        event.setCancelled(true);
        String input = PLAIN_TEXT.serialize(event.message());
        Bukkit.getScheduler().runTask(plugin, () -> applyInput(player, pendingInput, input));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        pendingInputs.remove(event.getPlayer().getUniqueId());
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
                inventory.setItem(menuSetting.slot, settingItem(menuSetting.setting));
            }
            inventory.setItem(BACK_SLOT, navigationItem(Material.ARROW, "Back", "Return to the main menu."));
        }

        player.openInventory(inventory);
    }

    private void openListEditor(Player player, Setting setting, MenuType returnMenu, int requestedPage) {
        List<String> values = plugin.getConfig().getStringList(setting.path);
        int lastPage = values.isEmpty() ? 0 : (values.size() - 1) / LIST_PAGE_SIZE;
        int page = Math.max(0, Math.min(requestedPage, lastPage));
        ConfigInventoryHolder holder = new ConfigInventoryHolder(setting, returnMenu, page);
        Inventory inventory = holder.getInventory();
        fillBackground(inventory);

        int firstIndex = page * LIST_PAGE_SIZE;
        int lastIndex = Math.min(firstIndex + LIST_PAGE_SIZE, values.size());
        for (int index = firstIndex; index < lastIndex; index++) {
            inventory.setItem(index - firstIndex, item(setting.material, "Entry " + (index + 1), List.of(
                    line(values.get(index).isEmpty() ? "<empty>" : abbreviate(values.get(index)), NamedTextColor.AQUA),
                    Component.empty(),
                    line("Left-click to edit.", NamedTextColor.YELLOW),
                    line("Right-click to remove.", NamedTextColor.RED)
            )));
        }

        if (page > 0) {
            inventory.setItem(LIST_PREVIOUS_SLOT, navigationItem(Material.ARROW, "Previous Page", "View earlier entries."));
        }
        inventory.setItem(LIST_ADD_SLOT, navigationItem(Material.LIME_DYE, "Add Entry", "Add another value."));
        inventory.setItem(LIST_BACK_SLOT, navigationItem(Material.ARROW, "Back", "Return to the previous menu."));
        inventory.setItem(LIST_CLEAR_SLOT, item(Material.BARRIER, "Clear All", List.of(
                line("Remove every entry from this list.", NamedTextColor.GRAY),
                Component.empty(),
                line("Shift-click to clear.", NamedTextColor.RED)
        )));
        if (page < lastPage) {
            inventory.setItem(LIST_NEXT_SLOT, navigationItem(Material.ARROW, "Next Page", "View later entries."));
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
            return;
        }

        if (setting.valueType == ValueType.STRING_LIST) {
            openListEditor(player, setting, returnMenu, 0);
            return;
        }

        beginInput(player, new PendingInput(setting, returnMenu, InputAction.SET_VALUE, -1, 0));
    }

    private void handleListEditorClick(Player player, InventoryClickEvent event, ConfigInventoryHolder holder) {
        Setting setting = holder.listSetting;
        List<String> values = new ArrayList<>(plugin.getConfig().getStringList(setting.path));
        int slot = event.getRawSlot();
        int valueIndex = holder.page * LIST_PAGE_SIZE + slot;

        if (slot < LIST_PAGE_SIZE && valueIndex < values.size()) {
            if (event.isRightClick()) {
                values.remove(valueIndex);
                saveList(setting, values);
                player.sendMessage(Component.text("Entry removed.", NamedTextColor.GREEN));
                openListEditor(player, setting, holder.returnMenu, holder.page);
            } else {
                beginInput(player, new PendingInput(setting, holder.returnMenu, InputAction.EDIT_LIST_ENTRY,
                        valueIndex, holder.page));
            }
            return;
        }

        switch (slot) {
            case LIST_PREVIOUS_SLOT -> openListEditor(player, setting, holder.returnMenu, holder.page - 1);
            case LIST_ADD_SLOT -> beginInput(player, new PendingInput(setting, holder.returnMenu,
                    InputAction.ADD_LIST_ENTRY, -1, holder.page));
            case LIST_BACK_SLOT -> openMenu(player, holder.returnMenu);
            case LIST_CLEAR_SLOT -> {
                if (event.isShiftClick()) {
                    saveList(setting, List.of());
                    player.sendMessage(Component.text(setting.displayName + " cleared.", NamedTextColor.GREEN));
                    openListEditor(player, setting, holder.returnMenu, 0);
                }
            }
            case LIST_NEXT_SLOT -> openListEditor(player, setting, holder.returnMenu, holder.page + 1);
            default -> {
            }
        }
    }

    private void beginInput(Player player, PendingInput pendingInput) {
        pendingInputs.put(player.getUniqueId(), pendingInput);
        player.closeInventory();

        if (pendingInput.action == InputAction.ADD_LIST_ENTRY) {
            player.sendMessage(Component.text("Enter the new " + pendingInput.setting.displayName + " entry in chat.", NamedTextColor.YELLOW));
        } else if (pendingInput.action == InputAction.EDIT_LIST_ENTRY) {
            player.sendMessage(Component.text("Enter a replacement for entry " + (pendingInput.listIndex + 1) + " in chat.", NamedTextColor.YELLOW));
        } else {
            player.sendMessage(Component.text("Enter a new value for " + pendingInput.setting.displayName + " in chat.", NamedTextColor.YELLOW));
        }

        if (pendingInput.setting.valueType == ValueType.SOUND) {
            player.sendMessage(Component.text("Use a sound key such as minecraft:entity.experience_orb.pickup.", NamedTextColor.GRAY));
            player.sendMessage(Component.text("Type cancel to return without saving.", NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("Type <empty> for an empty value or cancel to return without saving.", NamedTextColor.GRAY));
        }
    }

    private void applyInput(Player player, PendingInput pendingInput, String input) {
        if (!player.isOnline()) {
            return;
        }

        Setting setting = pendingInput.setting;
        if (input.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("No changes were saved.", NamedTextColor.YELLOW));
            returnToPendingMenu(player, pendingInput);
            return;
        }

        String stringValue = input.equalsIgnoreCase("<empty>") ? "" : input.trim();
        if (pendingInput.action == InputAction.SET_VALUE) {
            if (setting.valueType == ValueType.SOUND) {
                NamespacedKey soundKey = NamespacedKey.fromString(stringValue);
                if (soundKey == null || Registry.SOUNDS.get(soundKey) == null) {
                    pendingInputs.put(player.getUniqueId(), pendingInput);
                    player.sendMessage(Component.text("That sound does not exist. Try another key or type cancel.", NamedTextColor.RED));
                    return;
                }
                stringValue = soundKey.toString();
            }

            plugin.getConfig().set(setting.path, stringValue);
        } else {
            List<String> values = new ArrayList<>(plugin.getConfig().getStringList(setting.path));
            if (pendingInput.action == InputAction.ADD_LIST_ENTRY) {
                values.add(stringValue);
            } else if (pendingInput.listIndex >= 0 && pendingInput.listIndex < values.size()) {
                values.set(pendingInput.listIndex, stringValue);
            } else {
                player.sendMessage(Component.text("That entry no longer exists.", NamedTextColor.RED));
                openListEditor(player, setting, pendingInput.returnMenu, pendingInput.page);
                return;
            }
            plugin.getConfig().set(setting.path, values);
        }

        plugin.saveConfig();
        player.sendMessage(Component.text(setting.displayName + " saved.", NamedTextColor.GREEN));
        returnToPendingMenu(player, pendingInput);
    }

    private void returnToPendingMenu(Player player, PendingInput pendingInput) {
        if (pendingInput.action == InputAction.SET_VALUE) {
            openMenu(player, pendingInput.returnMenu);
        } else {
            openListEditor(player, pendingInput.setting, pendingInput.returnMenu, pendingInput.page);
        }
    }

    private void saveList(Setting setting, List<String> values) {
        plugin.getConfig().set(setting.path, values);
        plugin.saveConfig();
    }

    private ItemStack settingItem(Setting setting) {
        List<Component> lore = new ArrayList<>();
        if (setting.valueType == ValueType.BOOLEAN) {
            boolean enabled = plugin.getConfig().getBoolean(setting.path);
            lore.add(line(enabled ? "Enabled" : "Disabled", enabled ? NamedTextColor.GREEN : NamedTextColor.RED));
            lore.add(Component.empty());
            lore.add(line("Click to toggle.", NamedTextColor.YELLOW));
        } else if (setting.valueType == ValueType.STRING_LIST) {
            List<String> values = plugin.getConfig().getStringList(setting.path);
            lore.add(line(values.size() + (values.size() == 1 ? " entry" : " entries"), NamedTextColor.AQUA));
            values.stream().limit(2).forEach(value -> lore.add(line("• " + abbreviate(value), NamedTextColor.GRAY)));
            if (values.size() > 2) {
                lore.add(line("• ...", NamedTextColor.GRAY));
            }
            lore.add(Component.empty());
            lore.add(line("Click to manage.", NamedTextColor.YELLOW));
        } else {
            String value = plugin.getConfig().getString(setting.path, "");
            lore.add(line(value.isEmpty() ? "<empty>" : abbreviate(value), NamedTextColor.AQUA));
            lore.add(Component.empty());
            lore.add(line("Click to edit.", NamedTextColor.YELLOW));
        }

        return item(setting.material, setting.displayName, lore);
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
        LIST_EDITOR("Edit List");

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

    private enum InputAction {
        SET_VALUE,
        ADD_LIST_ENTRY,
        EDIT_LIST_ENTRY
    }

    private record Setting(String path, String displayName, Material material, ValueType valueType) {
    }

    private record MenuSetting(int slot, Setting setting) {
    }

    private record PendingInput(Setting setting, MenuType returnMenu, InputAction action, int listIndex, int page) {
    }

    private static final class ConfigInventoryHolder implements InventoryHolder {
        private final MenuType menuType;
        private final Setting listSetting;
        private final MenuType returnMenu;
        private final int page;
        private final Inventory inventory;

        private ConfigInventoryHolder(MenuType menuType) {
            this.menuType = menuType;
            this.listSetting = null;
            this.returnMenu = null;
            this.page = 0;
            this.inventory = Bukkit.createInventory(this, MENU_SIZE, Component.text(menuType.title));
        }

        private ConfigInventoryHolder(Setting listSetting, MenuType returnMenu, int page) {
            this.menuType = MenuType.LIST_EDITOR;
            this.listSetting = listSetting;
            this.returnMenu = returnMenu;
            this.page = page;
            this.inventory = Bukkit.createInventory(this, LIST_MENU_SIZE, Component.text(listSetting.displayName));
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}

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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.abradee.joinLeavePlus.JoinLeavePlus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Opens an editable client-side book without changing the player's real inventory.
 * The incoming edit packet is consumed before Minecraft validates the spoofed book.
 */
final class VirtualBookEditor {
    static final int MAX_PAGES = 100;
    private static final long OPEN_TIMEOUT_TICKS = 20L * 20L;
    private static final long SESSION_TIMEOUT_TICKS = 20L * 60L * 5L;

    private final JoinLeavePlus plugin;
    private final ProtocolManager protocolManager;
    private final Map<UUID, EditSession> sessions = new ConcurrentHashMap<>();

    VirtualBookEditor(JoinLeavePlus plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        registerPacketListener();
    }

    void open(Player player, List<String> pages, Consumer<EditResult> completion) {
        if (pages.size() > MAX_PAGES) {
            throw new IllegalArgumentException("A writable book can contain at most " + MAX_PAGES + " pages.");
        }

        cancel(player.getUniqueId());
        int heldSlot = player.getInventory().getHeldItemSlot();
        ItemStack heldItem = player.getInventory().getItem(heldSlot);
        ItemStack actualItem = heldItem == null ? new ItemStack(Material.AIR) : heldItem.clone();
        EditSession session = new EditSession(heldSlot, actualItem, completion, new AtomicBoolean());
        sessions.put(player.getUniqueId(), session);

        ItemStack virtualBook = createBook(pages);
        int clientSlot = 36 + heldSlot;

        // The vanilla client opens the editable screen locally when this spoofed item is used.
        sendSlot(player, clientSlot, virtualBook);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!session.opened().get() && sessions.remove(player.getUniqueId(), session)) {
                restoreSlot(player, session);
            }
        }, OPEN_TIMEOUT_TICKS);
    }

    void cancel(UUID playerId) {
        EditSession session = sessions.remove(playerId);
        Player player = Bukkit.getPlayer(playerId);
        if (session != null && !session.opened().get() && player != null && player.isOnline()) {
            restoreSlot(player, session);
        }
    }

    void shutdown() {
        sessions.forEach((playerId, session) -> {
            Player player = Bukkit.getPlayer(playerId);
            if (!session.opened().get() && player != null && player.isOnline()) {
                restoreSlot(player, session);
            }
        });
        sessions.clear();
        protocolManager.removePacketListeners(plugin);
    }

    private void registerPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Client.B_EDIT,
                PacketType.Play.Client.USE_ITEM,
                PacketType.Play.Client.USE_ITEM_ON,
                PacketType.Play.Client.USE_ENTITY,
                PacketType.Play.Client.HELD_ITEM_SLOT,
                PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                EditSession session = sessions.get(player.getUniqueId());
                if (session == null) {
                    return;
                }

                if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
                    handleUseItem(event, player, session);
                } else if (event.getPacketType() == PacketType.Play.Client.B_EDIT) {
                    handleBookEdit(event, player, session);
                } else if (!session.opened().get()) {
                    protectSpoofedSlot(event, player, session);
                }
            }
        });
    }

    private void handleUseItem(PacketEvent event, Player player, EditSession session) {
        if (session.opened().get()
                || event.getPacket().getHands().read(0) != EnumWrappers.Hand.MAIN_HAND
                || !session.opened().compareAndSet(false, true)) {
            return;
        }

        // The client has already opened its local editor; stop the real held item being used server-side.
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, () -> restoreSlot(player, session));
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> sessions.remove(player.getUniqueId(), session), SESSION_TIMEOUT_TICKS);
    }

    private void handleBookEdit(PacketEvent event, Player player, EditSession session) {
        if (!session.opened().get()
                || event.getPacket().getIntegers().read(0) != session.heldSlot()) {
            return;
        }

        event.setCancelled(true);
        if (!sessions.remove(player.getUniqueId(), session)) {
            return;
        }

        List<String> pages = List.copyOf(event.getPacket()
                .getLists(Converters.passthrough(String.class)).read(0));
        Optional<String> title = event.getPacket()
                .getOptionals(Converters.passthrough(String.class)).read(0);
        EditResult result = new EditResult(pages, title.isPresent());

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                session.completion().accept(result);
            }
        });
    }

    private void protectSpoofedSlot(PacketEvent event, Player player, EditSession session) {
        if (event.getPacketType() == PacketType.Play.Client.USE_ITEM_ON
                || event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
            event.setCancelled(true);
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_SLOT) {
            if (sessions.remove(player.getUniqueId(), session)) {
                Bukkit.getScheduler().runTask(plugin, () -> restoreSlot(player, session));
            }
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
            EnumWrappers.PlayerDigType digType = event.getPacket().getPlayerDigTypes().read(0);
            if (digType == EnumWrappers.PlayerDigType.DROP_ITEM
                    || digType == EnumWrappers.PlayerDigType.DROP_ALL_ITEMS
                    || digType == EnumWrappers.PlayerDigType.SWAP_HELD_ITEMS) {
                event.setCancelled(true);
            }
        }
    }

    private void restoreSlot(Player player, EditSession session) {
        if (!player.isOnline()) {
            return;
        }
        sendSlot(player, 36 + session.heldSlot(), session.actualItem());
    }

    private void sendSlot(Player player, int slot, ItemStack item) {
        if (!player.isOnline()) {
            return;
        }
        PacketContainer setSlot = protocolManager.createPacket(PacketType.Play.Server.SET_SLOT);
        setSlot.getIntegers()
                .write(0, 0)
                .write(1, 0)
                .write(2, slot);
        setSlot.getItemModifier().write(0, item);
        protocolManager.sendServerPacket(player, setSlot);
    }

    private static ItemStack createBook(List<String> pages) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setPages(pages.isEmpty() ? List.of("") : new ArrayList<>(pages));
        book.setItemMeta(meta);
        return book;
    }

    record EditResult(List<String> pages, boolean signed) {
    }

    private record EditSession(int heldSlot, ItemStack actualItem, Consumer<EditResult> completion,
                               AtomicBoolean opened) {
    }
}

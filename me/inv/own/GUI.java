package me.inv.own;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;

public class GUI implements Listener {

    public static void openMainInventory(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Queue");
        inv.setItem(12, getLocationItem());
        inv.setItem(14, getQueueItem());
        p.openInventory(inv);
    }

    public void openLocationInventory(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Queue");
        inv.setItem(11, getSetQueueLocationItem());
        inv.setItem(15, getIsAllowTeleport());
        p.openInventory(inv);
    }
    public static void openQueueInventory(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Queue");
        inv.setItem(10, getMaxPlayers());
        inv.setItem(13, getenQueuedPlayersItem());
        inv.setItem(16, getAllowEffectsItem());
        p.openInventory(inv);
    }

    public static void openPlayersMenu(Player p, int page) {
        p.setCompassTarget(p.getLocation());
        Inventory inv = Bukkit.createInventory(null, 54, "Queue - Page " + page);
        ArrayList<Player> players = Queue.getQueuePlayers();
        for (int i = 0; i < 45; i++) {
            if (i >= players.size()) {
                ItemStack black = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                ItemMeta mblack = black.getItemMeta();
                mblack.setDisplayName("§0");
                black.setItemMeta(mblack);
                inv.setItem(i, black);
            } else {
                int x = (i + ((page - 1)*45));
                Player player = players.get(i);
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta mitem = (SkullMeta) item.getItemMeta();
                mitem.setOwner(player.getName());
                mitem.setDisplayName("§8>> §6" + player.getName() + ((Config.isPriority(player.getName()    ))? " §8[§aPriority§8]" : ""));
                ArrayList<String> litem = new ArrayList<String>();
                litem.add("§eClick to deQueue!");
                mitem.setLore(litem);
                item.setItemMeta(mitem);
                inv.setItem(i, item);
            }
        }
        inv.setItem(45, getSlideItem(false));
        inv.setItem(53, getSlideItem(true));
        inv.setItem(49, getRefreshItem());
        p.openInventory(inv);
    }

    //Events
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack clicked = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().contains("Queue")) {
            e.setCancelled(true);
            if (clicked.isSimilar(getMaxPlayers())) {
                if (e.getClick() == ClickType.LEFT) {
                    Config.setMaxPlayers(Config.getMaxPlayers() + 1);
                } else if (e.getClick() == ClickType.SHIFT_LEFT) {
                    Config.setMaxPlayers(Config.getMaxPlayers() + 10);
                } else if (e.getClick() == ClickType.RIGHT) {
                    Config.setMaxPlayers(Config.getMaxPlayers() - 1);
                } else if (e.getClick() == ClickType.SHIFT_RIGHT) {
                    Config.setMaxPlayers(Config.getMaxPlayers() - 10);
                } else if (e.getClick() == ClickType.MIDDLE) {
                    Config.setMaxPlayers(0);
                }
                clicked.setItemMeta(getMaxPlayers().getItemMeta());
            } else if (clicked.isSimilar(getAllowEffectsItem())) {
                Config.toggleBLindAllowed();
                clicked.setItemMeta(getAllowEffectsItem().getItemMeta());
                clicked.setType(getAllowEffectsItem().getType());
                if (Config.isBlindAllowed()) p.sendMessage("§8| §aQueue §8| §7Teleport: §aEnabled");
                else p.sendMessage("§8| §aQueue §8| §7Teleport: §cDisabled");
                p.closeInventory();
            } else if (clicked.isSimilar(getenQueuedPlayersItem())) {
                if (!Queue.getQueuePlayers().isEmpty()) {
                    openPlayersMenu(p, 1);
                } else {
                    p.sendMessage("§8| §cQueue §8| §7There's no players in the Queue currently");
                    p.closeInventory();
                }
            } else if (clicked.isSimilar(getSetQueueLocationItem())) {
                Location loc = p.getLocation();
                Config.setLocation(loc, true);
                p.sendMessage("§8| §aQueue §8| §7Queue Location has been set to (§e" + loc.getBlockX() + "§7, §e" + loc.getBlockY() + "§7, §e" + loc.getBlockZ() + "§7)");
                p.closeInventory();
            } else if (clicked.isSimilar(getIsAllowTeleport())) {
                Config.toggleTPAllowed();
                clicked.setItemMeta(getIsAllowTeleport().getItemMeta());
                if (Config.isTPAllowed()) p.sendMessage("§8| §aQueue §8| §7Teleport: §aEnabled");
                else p.sendMessage("§8| §aQueue §8| §7Teleport: §cDisabled");
                p.closeInventory();
            } else if (clicked.isSimilar(getLocationItem())) {
                openLocationInventory(p);
            } else if (clicked.isSimilar(getQueueItem())) {
                openQueueInventory(p);
            }else if (clicked.isSimilar(getRefreshItem())) {
                int page = Integer.parseInt(e.getView().getTitle().split("Queue - Page ")[1]);
                openPlayersMenu(p, page);
            } else if (clicked.isSimilar(getSlideItem(true))) {
                int page = Integer.parseInt(e.getView().getTitle().split("Queue - Page ")[1]);
                if (Queue.getQueuePlayers().size() > page*45) {
                    openPlayersMenu(p, page + 1);
                } else {
                    p.sendMessage("§8| §cQueue §8| §7There's no next page");
                }
            } else if (clicked.isSimilar(getSlideItem(false))) {
                int page = Integer.parseInt(e.getView().getTitle().split("Queue - Page ")[1]);
                if (page > 1) {
                    openPlayersMenu(p, page - 1);
                } else {
                    p.sendMessage("§8| §cQueue §8| §7You are already in the first page");
                }
            } else if (clicked.getType() == Material.PLAYER_HEAD) {
                try {
                    Player player = Bukkit.getPlayer(clicked.getItemMeta().getDisplayName().replace("§8>> §6", "").replace(" §8[§aPriority§8]", ""));
                    if (player == null) {
                        openQueueInventory(p);
                        return;
                    }
                    Queue.deQueue(player);
                    p.closeInventory();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Items
    public static ItemStack getSlideItem(boolean next) {
        ItemStack item = new ItemStack(Material.ACACIA_SIGN);
        ItemMeta mitem = item.getItemMeta();
        if (next) {
            mitem.setDisplayName("§eNext Page §b§l>>");
        } else {
            mitem.setDisplayName("§b§l<< §ePrevious Page");
        }
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getRefreshItem() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§aRefresh");
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getenQueuedPlayersItem() {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §ePlayers in Queue");
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§7View Players waiting in the queue");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getMaxPlayers() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §eMax Players: §7" + Config.getMaxPlayers());
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§eLeft-Click: §7increase by 1");
        litem.add("§eRight-Click: §7decrease by 1");
        litem.add("§eShift-Click: §7increase/decrease by 10");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getAllowEffectsItem() {
        ItemStack item;
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§7Allow blinding effect while being in queue");
        if (Config.isBlindAllowed()) {
            item = new ItemStack(Material.HONEY_BOTTLE);
            litem.add("§7Status: §aON");
        } else {
            item = new ItemStack(Material.GLASS_BOTTLE);
            litem.add("§7Status: §cOFF");
        }
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §eAllow Effects");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getSetQueueLocationItem() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §eSet Queue Location");
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§7Players will teleport to this location");
        litem.add("§7when they §aJOIN §7to the queue");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getIsAllowTeleport() {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §eAllow Teleport");
        ArrayList<String> litem = new ArrayList<>();
        if (Config.isTPAllowed()) {
            litem.add("§7This Plugin is §aALLOWED §7from teleporting players");
        } else {
            litem.add("§7This Plugin is §cNOT ALLOWED §7from teleporting players");
        }
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getLocationItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §eLocation Settings");
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§8* §7Set Spawn Location");
        litem.add("§8* §7Set Queue Location");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getQueueItem() {
        ItemStack item = new ItemStack(Material.REPEATER);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §eQueue Settings");
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§8* §7Set Max Players");
        litem.add("§8* §7View Queued Players");
        litem.add("§8* §7Allow Blind Effect");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }

    public static ItemStack getPriorityItem() {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta mitem = item.getItemMeta();
        mitem.setDisplayName("§8> §ePriority Settings");
        ArrayList<String> litem = new ArrayList<>();
        litem.add("§8* §7Add Player");
        litem.add("§8* §7Remove Player");
        litem.add("§8* §7View Priority List");
        mitem.setLore(litem);
        item.setItemMeta(mitem);
        return item;
    }
}

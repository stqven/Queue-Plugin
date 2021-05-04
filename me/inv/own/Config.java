package me.inv.own;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;

public class Config {
    //Priority functions ...
    public static ArrayList<String> getPriority() {
        if (Main.getInstance().getConfig().contains("priority-list"))
            return (ArrayList<String>) Main.getInstance().getConfig().getList("priority-list");
        else return new ArrayList<>();
    }

    public static boolean isPriority(String pName) {
        ArrayList<String> priorityList = getPriority();
        for (String str : priorityList) {
            if (str.equals(pName)) return true;
        }
        return false;
    }

    public static boolean addPriority(String pName) {
        if (!isPriority(pName)) {
            List<String> priorityList = getPriority();
            priorityList.add(pName);
            Main.getInstance().getConfig().set("priority-list", priorityList);
            if (Queue.normalQueue.contains(pName)) {
                Queue.normalQueue.remove(pName);
                Player p = Bukkit.getPlayer(pName);
                if (p != null) Queue.enQueue(p);
            }
            Main.getInstance().saveConfig();
            return true;
        } else return false;
    }

    public static boolean removePriority(String pName) {
        if (isPriority(pName)) {
            List<String> priorityList = getPriority();
            priorityList.remove(pName);
            Main.getInstance().getConfig().set("priority-list", priorityList);
            if (Queue.priorityQueue.contains(pName)) {
                Queue.priorityQueue.remove(pName);
                Player p = Bukkit.getPlayer(pName);
                if (p != null) Queue.enQueue(p);
            }
            Main.getInstance().saveConfig();
            return true;
        } else return false;
    }

    //Max Players functions
    public static int getMaxPlayers() {
        return Main.getInstance().getConfig().getInt("max-players");
    }

    public static void setMaxPlayers(int value) {
        if (value < 1) value = 1;
        Main.getInstance().getConfig().set("max-players", value);
        Main.getInstance().saveConfig();
    }

    //Location functions
    public static void setLocation(Location loc, boolean queue) {
        String locName = "queue";
        if (!queue) {
            locName = "spawn";
        }
        Main.getInstance().getConfig().set(locName + "-location.world",loc.getWorld().getName());
        Main.getInstance().getConfig().set(locName + "-location.x", loc.getX());
        Main.getInstance().getConfig().set(locName + "-location.y", loc.getY());
        Main.getInstance().getConfig().set(locName + "-location.z", loc.getZ());
        Main.getInstance().getConfig().set(locName + "-location.yaw", loc.getYaw());
        Main.getInstance().getConfig().set(locName + "-location.pitch", loc.getPitch());
        Main.getInstance().saveConfig();
    }

    public static Location getLocation(boolean queue) {
        String locName = "queue";
        if (!queue) {
            locName = "spawn";
        }
        try {
            World world = Bukkit.getWorld(Main.getInstance().getConfig().getString(locName + "-location.world"));
            double x = Main.getInstance().getConfig().getDouble(locName + "-location.x");
            double y = Main.getInstance().getConfig().getDouble(locName + "-location.y");
            double z = Main.getInstance().getConfig().getDouble(locName + "-location.z");
            double yaw = Main.getInstance().getConfig().getDouble(locName + "-location.yaw");
            double pitch = Main.getInstance().getConfig().getDouble(locName + "-location.pitch");
            return new Location(world, x, y, z, (float) yaw, (float) pitch);
        } catch (Exception ex) {
            return null;
        }
    }

    //Toggle functions
    public static boolean isTPAllowed() {
        return Main.getInstance().getConfig().getBoolean("allow-teleport");
    }
    public static void toggleTPAllowed() {
        Main.getInstance().getConfig().set("allow-teleport", !isTPAllowed());
        Main.getInstance().saveConfig();
    }

    //Toggle functions
    public static boolean isChatDenied() {
        return Main.getInstance().getConfig().getBoolean("deny-chat");
    }
    public static void toggleDenyChat() {
        Main.getInstance().getConfig().set("deny-chat", !isChatDenied());
        Main.getInstance().saveConfig();
    }

    public static boolean isCommandsDenied() {
        return Main.getInstance().getConfig().getBoolean("deny-commands");
    }
    public static void toggleDenyCommands() {
        Main.getInstance().getConfig().set("deny-commands", !isCommandsDenied());
        Main.getInstance().saveConfig();
    }

    public static boolean isBlindAllowed() {
        return Main.getInstance().getConfig().getBoolean("allow-blind");
    }
    public static void toggleBLindAllowed() {
        boolean isb = isBlindAllowed();
        Main.getInstance().getConfig().set("allow-blind", !isb);
        Main.getInstance().saveConfig();
        ArrayList<Player> players = Queue.getQueuePlayers();
        if (isb) {
            for (Player p : players) {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        } else {
            for (Player p : players) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, true, false));
            }
        }
    }

    public static boolean hidePlayers() {
        return Main.getInstance().getConfig().getBoolean("hide-players");
    }

    //Messages
    public static void updateTitle(Player p) {
        if (p == null) {
            ArrayList<Player> queued = Queue.getQueuePlayers();
            if (queued.isEmpty()) return;
            for (Player pt : Queue.getQueuePlayers()) {
                pt.sendTitle(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Titles.title").replaceAll("%order%", Integer.toString(Queue.getOrder(pt)))), ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Titles.subtitle").replaceAll("%order%", Integer.toString(Queue.getOrder(pt)))), 1, 999999999, 2);
            }
        } else {
            p.sendTitle(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Titles.title").replaceAll("%order%", Integer.toString(Queue.getOrder(p)))), ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Titles.subtitle").replaceAll("%order%", Integer.toString(Queue.getOrder(p)))), 1, 999999999, 2);
        }
    }

    public static ArrayList<String> getQueueHelpMessages() {
        ArrayList<String> msgs = (ArrayList<String>) Main.getInstance().getConfig().getList("Messages.queue.help"), nmsg = new ArrayList<>();
        for (String msg : msgs) {
            nmsg.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
        return nmsg;
    }

    public static ArrayList<String> getPriorityHelpMessages() {
        ArrayList<String> msgs = (ArrayList<String>) Main.getInstance().getConfig().getList("Messages.priority.help"), nmsg = new ArrayList<>();
        for (String msg : msgs) {
            nmsg.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
        return nmsg;
    }

    public static String getToggleBlindEnabledMessage(boolean enabled) {
        return ChatColor.translateAlternateColorCodes('&', (Main.getInstance().getConfig().getString("Messages.toggle-blind." + (enabled? "enabled" : "disabled"))));
    }

    public static String getToggleTPEnabledMessage(boolean enabled) {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.toggle-teleport." + (enabled? "enabled" : "disabled")));
    }

    public static String getEmptyQueueMessage() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.queue.empty"));
    }

    public static String getSetMaxPlayersMessage(boolean success, int value) {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.set-max-players." + (success? "success" : "error")).replace("%value%", Integer.toString(value)));
    }

    public static String getSetLocationMessage(boolean queue, int x, int y, int z) {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.set-location." + (queue? "queue" : "spawn")).replaceAll("%x%", Integer.toString(x)).replaceAll("%y%", Integer.toString(y)).replaceAll("%z%", Integer.toString(z)));
    }

    public static String getPriorityAddMessage(boolean success, String name) {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.priority.add." + (success? "success" : "error")).replaceAll("%name%", name));
    }

    public static String getPriorityRemoveMessage(boolean success, String name) {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.priority.remove." + (success? "success" : "error")).replaceAll("%name%", name));
    }

    public static String getConfigReloadedMessage() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.config-reloaded"));
    }

    public static String getDenyChatMessage() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.deny-chat"));
    }

    public static String getDenyCommandsMessage() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.deny-commands"));
    }

    public static String getWelcomeMessage() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.welcome"));
    }

    public static String getNoPermsMessage() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("Messages.no-permissions"));
    }

    public static String getAdminPermission() {
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("admin-permission"));
    }
}

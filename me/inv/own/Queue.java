package me.inv.own;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Queue {
    static ArrayList<Player> priorityQueue = new ArrayList<>(), normalQueue = new ArrayList<>();
    static HashMap<Player, Location> locs = new HashMap<>();

    public static void enQueue(Player p) {
        if (Queue.getQueuePlayers().contains(p)) return;
        if (Config.isPriority(p.getName())) {
            priorityQueue.add(p);
            Config.updateTitle(null);
        }
        else {
            normalQueue.add(p);
            Config.updateTitle(p);
        }
        if (Config.isTPAllowed()) {
            Location loc = Config.getLocation(true);
            if (loc != null) {
                p.teleport(Config.getLocation(true));
            } else {
                p.sendMessage("§cThe spawn location is not yet");
            }
        }
        if (Config.hidePlayers()) {
            Bukkit.getServer().getOnlinePlayers().forEach(dest -> {
                if (dest != p) p.hidePlayer(dest);
            });
        }
        if (Config.isBlindAllowed()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, true, false));
        }
    }

    public static void deQueue() {
        if (!getQueuePlayers().isEmpty()) {
            Player p = getQueuePlayers().get(0);
            if (p != null) {
                p.teleport(Queue.locs.get(p));
                Queue.locs.remove(p);
                if (Config.hidePlayers()) {
                    Player finalP = p;
                    Bukkit.getOnlinePlayers().forEach(player -> finalP.showPlayer(player));
                }
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.sendTitle("", "", 0, 20, 0);
                p.sendMessage(Config.getWelcomeMessage());
            }
            if (priorityQueue.isEmpty()) {
                normalQueue.remove(p);
            } else {
                priorityQueue.remove(p);
            }
            Config.updateTitle(null);
        }
    }

    public static void deQueue(Player p) {
        priorityQueue.removeIf(player -> player == p);
        normalQueue.removeIf(player -> player == p);
        Config.updateTitle(null);
        if (p != null) {
            p.teleport(Queue.locs.get(p));
            Queue.locs.remove(p);
            if (Config.hidePlayers()) {
                Player finalP = p;
                Bukkit.getOnlinePlayers().forEach(player -> finalP.showPlayer(player));
            }
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.sendTitle("", "", 0, 20, 0);
            p.sendMessage(Config.getWelcomeMessage());
        }
    }

    public static int getOrder(Player p) {
        for (int i = 1; i <= priorityQueue.size(); i++) {
            if (priorityQueue.get(i - 1) == p) return i;
        }
        for (int i = 1; i <= normalQueue.size(); i++) {
            if (normalQueue.get(i - 1) == p) return priorityQueue.size() + i;
        }
        return -1;
    }

    public static ArrayList<Player> getQueuePlayers() {
        ArrayList<Player> queuePlayers = new ArrayList<Player>();
        queuePlayers.addAll(priorityQueue);
        queuePlayers.addAll(normalQueue);
        return queuePlayers;
    }
}

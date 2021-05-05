package me.inv.own;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class Events implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.sendTitle("", "", 0, 20, 0);
        if (Bukkit.getServer().getOnlinePlayers().size() > Config.getMaxPlayers()) {
            Queue.locs.put(p, p.getLocation());
            Queue.enQueue(p);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Queue.locs.containsKey(p)) {
            p.teleport(Queue.locs.get(p));
            Queue.locs.remove(p);
        }
        if (Queue.getQueuePlayers().contains(p)) {
            Queue.normalQueue.removeIf(player -> player == p);
            Queue.priorityQueue.removeIf(player -> player == p);
            Config.updateTitle(null);
        } else {
            Queue.deQueue();
            Config.updateTitle(null);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (Queue.getQueuePlayers().contains(p)) e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (Queue.getQueuePlayers().contains(p)) e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onDenyChat(PlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Config.isChatDenied() && Queue.getQueuePlayers().contains(p)) {
            e.setCancelled(true);
            p.sendMessage(Config.getDenyChatMessage());
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onDenyChat(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (Config.isCommandsDenied()) {
            if (Queue.getQueuePlayers().contains(p)) {
                e.setCancelled(true);
                p.sendMessage(Config.getDenyCommandsMessage());
            }
        }
    }
}

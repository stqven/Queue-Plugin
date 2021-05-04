package me.inv.own;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    private static Main instance;

    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new GUI(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getCommand("queue").setExecutor(new Commands());
        getCommand("priority").setExecutor(new Commands());
    }

    public void onLoad() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }
}
package me.inv.own;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> helpMessages;
        Player p = (Player) sender;
        helpMessages = Config.getQueueHelpMessages();
        if (cmd.getName().equalsIgnoreCase("queue")) {
            if (!p.hasPermission(Config.getAdminPermission())) {
                p.sendMessage(Config.getNoPermsMessage());
                return true;
            }
            if (args.length == 0) {
                helpMessages.forEach(msg -> p.sendMessage(msg));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("GUI")) {
                    GUI.openMainInventory(p);
                } else if (args[0].equalsIgnoreCase("toggleEffects")) {
                    Config.toggleBLindAllowed();
                    if (Config.isBlindAllowed()) p.sendMessage(Config.getToggleBlindEnabledMessage(true));
                    else p.sendMessage(Config.getToggleBlindEnabledMessage(false));
                } else if (args[0].equalsIgnoreCase("toggleTeleport")) {
                    Config.toggleTPAllowed();
                    if (Config.isTPAllowed()) p.sendMessage(Config.getToggleTPEnabledMessage(true));
                    else p.sendMessage(Config.getToggleTPEnabledMessage(false));
                } else if (args[0].equalsIgnoreCase("viewQueued")) {
                    if (!Queue.getQueuePlayers().isEmpty()) {
                        GUI.openPlayersMenu(p, 1);
                    } else {
                        p.sendMessage(Config.getEmptyQueueMessage());
                    }
                } else if (args[0].equalsIgnoreCase("setMaxPlayers")) {
                    p.sendMessage(helpMessages.get(1));
                } else if (args[0].equalsIgnoreCase("setLoc")) {
                    p.sendMessage(helpMessages.get(4));
                } else if (args[0].equalsIgnoreCase("reload")) {
                    Main.getInstance().reloadConfig();
                    p.sendMessage(Config.getConfigReloadedMessage());
                    Config.updateTitle(null);
                } else {
                    helpMessages.forEach(msg -> p.sendMessage(msg));
                }
            } else {
                if (args[0].equalsIgnoreCase("GUI")) {
                    GUI.openMainInventory(p);
                } else if (args[0].equalsIgnoreCase("toggleEffects")) {
                    Config.toggleBLindAllowed();
                    if (Config.isBlindAllowed()) p.sendMessage(Config.getToggleBlindEnabledMessage(true));
                    else p.sendMessage(Config.getToggleBlindEnabledMessage(false));
                } else if (args[0].equalsIgnoreCase("toggleTeleport")) {
                    Config.toggleTPAllowed();
                    if (Config.isTPAllowed()) p.sendMessage(Config.getToggleTPEnabledMessage(true));
                    else p.sendMessage(Config.getToggleTPEnabledMessage(false));
                } else if (args[0].equalsIgnoreCase("viewQueued")) {
                    if (!Queue.getQueuePlayers().isEmpty()) {
                        GUI.openPlayersMenu(p, 1);
                    } else {
                        p.sendMessage(Config.getEmptyQueueMessage());
                    }
                } else if (args[0].equalsIgnoreCase("setMaxPlayers")) {
                    try {
                        int value = Integer.parseInt(args[1]);
                        if (value < 1) value = 1;
                        Config.setMaxPlayers(value);
                        p.sendMessage(Config.getSetMaxPlayersMessage(true, value));
                    } catch (Exception ex) {
                        p.sendMessage(Config.getSetMaxPlayersMessage(false, 0));
                    }
                } else if (args[0].equalsIgnoreCase("setLoc")) {
                    Location loc = p.getLocation();
                    if (args[1].equalsIgnoreCase("spawn")) {
                        Config.setLocation(loc, false);
                        p.sendMessage(Config.getSetLocationMessage(false, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                    } else if (args[1].equalsIgnoreCase("queue")) {
                        Config.setLocation(p.getLocation(), true);
                        p.sendMessage(Config.getSetLocationMessage(true, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                    } else {
                        p.sendMessage(helpMessages.get(4));
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    Main.getInstance().reloadConfig();
                    p.sendMessage(Config.getConfigReloadedMessage());
                    Config.updateTitle(null);
                } else {
                    helpMessages.forEach(msg -> p.sendMessage(msg));
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("priority")) {
            if (!p.hasPermission(Config.getAdminPermission())) {
                p.sendMessage(Config.getNoPermsMessage());
                return true;
            }
            helpMessages = Config.getPriorityHelpMessages();
            if (args.length == 0) {
                helpMessages.forEach(msg -> p.sendMessage(msg));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    p.sendMessage(helpMessages.get(0));
                } else if (args[0].equalsIgnoreCase("remove")) {
                    p.sendMessage(helpMessages.get(1));
                } else {
                    helpMessages.forEach(msg -> p.sendMessage(msg));
                }
            } else {
                    if (args[0].equalsIgnoreCase("add")) {
                        if (Config.addPriority(args[1])) {
                            p.sendMessage(Config.getPriorityAddMessage(true, args[1]));
                        } else {
                            p.sendMessage(Config.getPriorityAddMessage(false, args[1]));
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (Config.removePriority(args[1])) {
                            p.sendMessage(Config.getPriorityRemoveMessage(true, args[1]));
                        } else {
                            p.sendMessage(Config.getPriorityRemoveMessage(false, args[1]));
                        }
                    } else {
                        helpMessages.forEach(msg -> p.sendMessage(msg));
                    }
                }
            }
        return true;
    }
}

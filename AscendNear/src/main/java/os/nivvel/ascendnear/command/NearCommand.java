package os.nivvel.ascendnear.command;

import org.bukkit.util.Vector;
import os.nivvel.ascendnear.AscendNear;
import os.nivvel.ascendnear.util.HexColorUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NearCommand implements CommandExecutor {

    private final AscendNear plugin;

    public NearCommand(AscendNear plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        int radius = 0;

        if (args.length == 1) {
            if (!player.hasPermission("AscendNear.radius.unlim")) {
                sendMessage(player, "no-custom-radius");
                return true;
            }
            try {
                radius = Integer.parseInt(args[0]);
                if (radius < 1 || radius > 1000) {
                    sendMessage(player, "invalid-radius");
                    return true;
                }
            } catch (NumberFormatException e) {
                sendMessage(player, "invalid-number");
                return true;
            }
        } else if (args.length > 1) {
            sendMessage(player, "usage");
            return true;
        } else {
            radius = getPlayerRadius(player);
            if (radius <= 0) {
                sendMessage(player, "no-permission");
                return true;
            }
        }

        List<PlayerInfo> nearby = getNearbyPlayers(player, radius);

        sendMessage(player, "header", "{radius}", String.valueOf(radius));

        if (nearby.isEmpty()) {
            sendMessage(player, "no-players");
            playSound(player, "no-players");
        } else {
            for (PlayerInfo pi : nearby) {
                sendHoverMessage(player, pi);
            }
            playSound(player, "found-players");
        }

        return true;
    }

    private void sendMessage(Player player, String key, String... replacements) {
        String path = "messages." + key;
        if (!plugin.getConfig().isSet(path)) return;
        String msg = plugin.getConfig().getString(path);
        for (int i = 0; i < replacements.length; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        // PAPI support
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            msg = PlaceholderAPI.setPlaceholders(player, msg);
        }
        HexColorUtils.sendMessage(player, msg);
    }

    private void sendHoverMessage(Player player, PlayerInfo pi) {
        String msgTemplate = plugin.getConfig().getString("messages.player-line", "* {arrow} {player} - {distance} blocks");
        msgTemplate = msgTemplate.replace("{arrow}", pi.arrow)
                .replace("{player}", pi.name)
                .replace("{distance}", String.valueOf((int) pi.distance));

        // PAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            msgTemplate = PlaceholderAPI.setPlaceholders(player, msgTemplate);
        }

        String hoverText = plugin.getConfig().getString("messages.hover-text", "Click to interact")
                .replace("{player}", pi.name);

        String commandClick = plugin.getConfig().getString("messages.click-command", "/invsee {player}")
                .replace("{player}", pi.name);

        // Parse HEX
        msgTemplate = HexColorUtils.translate(msgTemplate);
        hoverText = HexColorUtils.translate(hoverText);
        commandClick = HexColorUtils.translate(commandClick);

        player.spigot().sendMessage(new ComponentBuilder(msgTemplate)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(hoverText).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandClick))
                .create());
    }

    private void playSound(Player player, String key) {
        if (!plugin.getConfig().getBoolean("sounds.enabled", true)) return;
        String path = "sounds." + key;
        if (!plugin.getConfig().isSet(path + ".sound")) return;

        String soundName = plugin.getConfig().getString(path + ".sound");
        float volume = (float) plugin.getConfig().getDouble(path + ".volume", 1.0);
        float pitch = (float) plugin.getConfig().getDouble(path + ".pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name: " + soundName);
        }
    }

    private int getPlayerRadius(Player player) {
        int max = 0;
        if (plugin.getConfig().isConfigurationSection("permissions")) {
            for (String key : plugin.getConfig().getConfigurationSection("permissions").getKeys(false)) {
                if (player.hasPermission("AscendNear.radius." + key)) {
                    max = Math.max(max, plugin.getConfig().getInt("permissions." + key, 0));
                }
            }
        }
        return max;
    }

    private List<PlayerInfo> getNearbyPlayers(Player player, int radius) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        double maxDistSq = radius * radius;
        List<PlayerInfo> list = new ArrayList<>();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.equals(player)) continue;
            if (!target.getWorld().equals(world)) continue;
            if (target.hasPermission("ascendnear.near.hide")) continue;

            Location tLoc = target.getLocation();
            double dx = tLoc.getX() - loc.getX();
            double dz = tLoc.getZ() - loc.getZ();
            double distSq = dx * dx + dz * dz;
            if (distSq > maxDistSq || distSq == 0) continue;

            double distance = Math.sqrt(distSq);
            String arrow = getDirectionalArrow(player, target);
            list.add(new PlayerInfo(target.getName(), arrow, distance));
        }

        list.sort(Comparator.comparingDouble(pi -> pi.distance));
        return list;
    }

    private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};

    private String getDirectionalArrow(Player from, Player to) {
        Location locFrom = from.getLocation();
        Location locTo = to.getLocation();

        Vector direction = locTo.toVector().subtract(locFrom.toVector()).normalize();
        Vector facing = locFrom.getDirection().setY(0).normalize();

        double angle = Math.atan2(direction.getZ(), direction.getX()) - Math.atan2(facing.getZ(), facing.getX());
        angle = (angle + 2 * Math.PI) % (2 * Math.PI);

        int index = (int) Math.round(angle / (Math.PI / 4)) % 8;
        return ARROWS[index];
    }



    private static class PlayerInfo {
        final String name;
        final String arrow;
        final double distance;

        PlayerInfo(String name, String arrow, double distance) {
            this.name = name;
            this.arrow = arrow;
            this.distance = distance;
        }
    }
}

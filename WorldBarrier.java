package me.yourplugin.worldbarrier;

// By Star Dream Studio 
// https://xmc.tw

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldBarrier extends JavaPlugin implements Listener {

    private final Map<String, Integer> worldBarriers = new HashMap<>();

    @Override
    public void onEnable() {
        // ��������ļ������ڣ�������Ĭ������
        saveDefaultConfig();
        checkAndCreateConfig();
        
        loadWorldBarriers();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("WorldBarrier �����ã�");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldBarrier �ѽ��ã�");
    }

    private void loadWorldBarriers() {
        worldBarriers.clear();
        for (String world : getConfig().getConfigurationSection("world-barriers").getKeys(false)) {
            if (getConfig().getBoolean("world-barriers." + world + ".enabled")) {
                int limit = getConfig().getInt("world-barriers." + world + ".limit");
                worldBarriers.put(world, limit);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        String worldName = to.getWorld().getName();
        if (worldBarriers.containsKey(worldName)) {
            int limit = worldBarriers.get(worldName);
            if (Math.abs(to.getX()) > limit || Math.abs(to.getZ()) > limit) {
                player.teleport(to.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.RED + "���޷��뿪�߽磡�ѽ��㴫�ͻس����㡣");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("worldbarrier")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("worldbarrier.reload")) {
                    sender.sendMessage(ChatColor.RED + "��û��Ȩ��ִ�д����");
                    return true;
                }
                reloadConfig();
                loadWorldBarriers();
                sender.sendMessage(ChatColor.GREEN + "WorldBarrier ���������أ�");
                return true;
            }
        }
        return false;
    }

    private void checkAndCreateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getLogger().info("δ�ҵ������ļ�����������Ĭ������...");
            saveResource("config.yml", false);
        }
    }
}

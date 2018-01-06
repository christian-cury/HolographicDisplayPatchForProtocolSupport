package com.gmail.filoghost.holograms.patch;

import java.util.Collections;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

import protocolsupport.api.ProtocolSupportAPI;

public class HologramsPatch extends JavaPlugin implements Listener {

	private static Set<Player> oldProtocol;
	public static HologramsPatch plugin;

	@Override
	public void onEnable() {
		plugin = this;
		
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getLogger().severe("This plugin requires Holographic Displays to work!");
			setEnabled(false);
			return;
		}
		
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			getLogger().severe("This plugin requires ProtocolLib to work!");
			setEnabled(false);
			return;
		}

		Matcher buildMatcher = Pattern.compile("(?i)(git-Spigot-)(\\d+)").matcher(Bukkit.getVersion());
		if (!buildMatcher.find() || buildMatcher.groupCount() < 2 || !isCorrectBuild(buildMatcher.group(2))) {
			getLogger().warning("This plugin does only work on Spigot #1628 and higher! If you're using the new patched Spigot, ignore this warning.");
		}
		
		oldProtocol = Collections.synchronizedSet(new HashSet<Player>());
		ProtocolLibrary.getProtocolManager().addPacketListener(new HologramsPacketListener(this));
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	private boolean isCorrectBuild(String input) {
		try {
			return Integer.parseInt(input) >= 1628;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// Higher than 5 = new protocol
		if(ProtocolSupportAPI.getProtocolVersion(event.getPlayer()).getId() <= 5){
			oldProtocol.add(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if(oldProtocol.contains(event.getPlayer())){
			oldProtocol.remove(event.getPlayer());
		}
	}

	
	public static boolean hasOldProtocol(Player player) {
		return oldProtocol.contains(player);
	}
}

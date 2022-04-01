package me.mcblueparrot.bukkicord.event.converter;

import me.mcblueparrot.bukkicord.Bukkicord;
import me.mcblueparrot.bukkicord.event.PlayerMessageToDiscordEvent;
import me.mcblueparrot.bukkicord.event.impl.MinecraftToDiscordEvent;
import me.mcblueparrot.bukkicord.event.impl.PlayerJoinToDiscordEvent;
import me.mcblueparrot.bukkicord.event.impl.PlayerQuitToDiscordEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveConverter implements Listener {

	private Bukkicord plugin;

	public PlayerJoinLeaveConverter(Bukkicord plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(event.getJoinMessage() == null || event.getJoinMessage().isEmpty()) {
			return;
		}

		Bukkit.getScheduler().runTask(plugin, () -> plugin.sendEventToDiscord("join", new PlayerJoinToDiscordEvent(event.getPlayer())));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(event.getQuitMessage() == null || event.getQuitMessage().isEmpty()) {
			return;
		}

		plugin.sendEventToDiscord("quit", new PlayerQuitToDiscordEvent(event.getPlayer()));
	}

}

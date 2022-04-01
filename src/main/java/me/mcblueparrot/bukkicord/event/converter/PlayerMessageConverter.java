package me.mcblueparrot.bukkicord.event.converter;

import me.mcblueparrot.bukkicord.Bukkicord;
import me.mcblueparrot.bukkicord.event.PlayerMessageToDiscordEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerMessageConverter implements Listener {

	private Bukkicord plugin;

	public PlayerMessageConverter(Bukkicord plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		plugin.sendEventToDiscord("chat", new PlayerMessageToDiscordEvent(event.getPlayer(),
				event.getMessage()));
	}

}

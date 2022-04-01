package me.mcblueparrot.bukkicord.event.converter;

import me.mcblueparrot.bukkicord.Bukkicord;
import me.mcblueparrot.bukkicord.event.impl.MinecraftToDiscordEvent;
import me.mcblueparrot.bukkicord.event.impl.PlayerAdvancementToDiscordEvent;
import me.mcblueparrot.bukkicord.event.impl.PlayerDeathToDiscordEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerAdvancementConverter implements Listener {

	private Bukkicord plugin;

	public PlayerAdvancementConverter(Bukkicord plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
		// Skip an advancement if it is a recipe
		if(event.getAdvancement().getKey().getKey().startsWith("recipe")) {
			return;
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.sendEventToDiscord("advancement",
				new PlayerAdvancementToDiscordEvent(event.getPlayer(), event.getAdvancement())));
	}

}

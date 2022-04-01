package me.mcblueparrot.bukkicord.event.converter;

import me.mcblueparrot.bukkicord.Bukkicord;
import me.mcblueparrot.bukkicord.event.impl.MinecraftToDiscordEvent;
import me.mcblueparrot.bukkicord.event.impl.PlayerDeathToDiscordEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDeathConverter implements Listener {

	private Bukkicord plugin;

	public PlayerDeathConverter(Bukkicord plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if(event.getDeathMessage() == null || event.getDeathMessage().isEmpty()) {
			return;
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.sendEventToDiscord("death",
				new PlayerDeathToDiscordEvent(event.getEntity(), event.getDeathMessage())));
	}

}

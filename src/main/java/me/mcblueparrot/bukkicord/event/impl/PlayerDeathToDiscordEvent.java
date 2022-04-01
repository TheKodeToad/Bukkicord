package me.mcblueparrot.bukkicord.event.impl;

import org.bukkit.entity.Player;

import me.mcblueparrot.bukkicord.BukkicordPlaceholderExpansion;

/**
 * Death to Discord!
 */
public class PlayerDeathToDiscordEvent extends MinecraftToDiscordEvent {

	private String message;

	public PlayerDeathToDiscordEvent(Player player, String message) {
		super(player, true);
		this.message = message;
		addPlaceholder(new DeathPlaceholder());
	}

	private class DeathPlaceholder extends BukkicordPlaceholderExpansion {

		@Override
		public String getIdentifier() {
			return "death";
		}

		@Override
		public String onPlaceholderRequest(Player player, String identifier) {
			if(identifier.equals("message")) {
				return message;
			}

			return null;
		}
	}

}

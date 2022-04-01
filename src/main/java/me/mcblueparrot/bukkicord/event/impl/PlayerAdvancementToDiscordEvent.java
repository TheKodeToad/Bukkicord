package me.mcblueparrot.bukkicord.event.impl;

import java.util.Map;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import me.mcblueparrot.bukkicord.BukkicordPlaceholderExpansion;
import me.mcblueparrot.bukkicord.util.Utils;

public class PlayerAdvancementToDiscordEvent extends MinecraftToDiscordEvent {

	private Advancement advancement;

	public PlayerAdvancementToDiscordEvent(Player player, Advancement advancement) {
		super(player, true);
		this.advancement = advancement;
		addPlaceholder(new AdvancementPlaceholder());
	}

	public Advancement getAdvancement() {
		return advancement;
	}

	private class AdvancementPlaceholder extends BukkicordPlaceholderExpansion {

		@Override
		public String getIdentifier() {
			return "advancement";
		}

		@Override
		public String onPlaceholderRequest(Player player, String identifier) {
			if(identifier.equals("name")) {
				return Utils.getAdvancementName(advancement);
			}

			return null;
		}
	}

}

package me.mcblueparrot.bukkicord.event.impl;

import org.bukkit.entity.Player;

public class PlayerQuitToDiscordEvent extends MinecraftToDiscordEvent {

	public PlayerQuitToDiscordEvent(Player player) {
		super(player, false);
	}

}

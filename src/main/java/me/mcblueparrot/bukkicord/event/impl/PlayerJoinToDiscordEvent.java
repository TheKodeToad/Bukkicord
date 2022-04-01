package me.mcblueparrot.bukkicord.event.impl;

import org.bukkit.entity.Player;

public class PlayerJoinToDiscordEvent extends MinecraftToDiscordEvent {

	public PlayerJoinToDiscordEvent(Player player) {
		super(player, false);
	}

}

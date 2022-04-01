package me.mcblueparrot.bukkicord.event.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mcblueparrot.bukkicord.BukkicordPlaceholderExpansion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MinecraftToDiscordEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Player player;
	private Supplier<Map<TextChannel, Message>> discordMessages;
	private boolean cancelled;
	private Map<String, PlaceholderExpansion> placeholders = new HashMap<>();

	public MinecraftToDiscordEvent(Player player, boolean async) {
		super(async);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public Map<TextChannel, Message> getDiscordMessages() {
		return discordMessages.get();
	}

	public void setDiscordMessages(Supplier<Map<TextChannel, Message>> discordMessages) {
		this.discordMessages = discordMessages;
	}

	public void addPlaceholder(BukkicordPlaceholderExpansion placeholder) {
		this.placeholders.put(placeholder.getIdentifier(), placeholder);
	}

	public Map<String, PlaceholderExpansion> getPlaceholders() {
		return placeholders;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}

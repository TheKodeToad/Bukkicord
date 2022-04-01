package me.mcblueparrot.bukkicord.event;

import me.mcblueparrot.bukkicord.BukkicordPlaceholderExpansion;
import me.mcblueparrot.bukkicord.event.impl.MinecraftToDiscordEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PlayerMessageToDiscordEvent extends MinecraftToDiscordEvent {

	private Date date = new Date();
	private String message;

	public PlayerMessageToDiscordEvent(Player player, String message) {
		super(player, true);
		this.message = message;
		addPlaceholder(new MessagePlaceholder());
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private class MessagePlaceholder extends BukkicordPlaceholderExpansion {

		@Override
		public String getIdentifier() {
			return "message";
		}

		@Override
		public String onPlaceholderRequest(Player player, String identifier) {
			if(identifier.equals("content")) {
				return MarkdownSanitizer.escape(message).replace("@", "@\u200b");
			}
			else if(identifier.equals("date")) {
				return Long.toString(date.toInstant().toEpochMilli());
			}
			else if(identifier.equals("date_string")) {
				return date.toString();
			}

			return null;
		}
	}

}

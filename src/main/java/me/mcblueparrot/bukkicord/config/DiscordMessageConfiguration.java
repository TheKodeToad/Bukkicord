package me.mcblueparrot.bukkicord.config;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.replacer.CharsReplacer;
import me.clip.placeholderapi.replacer.Replacer;
import me.mcblueparrot.bukkicord.Bukkicord;
import me.mcblueparrot.bukkicord.event.impl.MinecraftToDiscordEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.awt.Color;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DiscordMessageConfiguration {

	private static final Replacer PERCENT_REPLACER = new CharsReplacer(Replacer.Closure.PERCENT);

	private Bukkicord plugin;
	private String channel;
	private TextChannel discordChannel;
	private String text;
	private List<EmbedConfiguration> embeds;

	public TextChannel getChannel() {
		if(discordChannel == null) {
			discordChannel = plugin.getChannel(channel);
		}

		return discordChannel;
	}

	public DiscordMessageConfiguration(Bukkicord plugin, String channel, String text, List<EmbedConfiguration> embeds) {
		this.plugin = plugin;
		this.channel = channel;
		this.text = text;
		this.embeds = embeds;
	}

	public Message getMessage(MinecraftToDiscordEvent event) {
		return new MessageBuilder()
				.setContent(processText(event, this.text))
				.setEmbeds(this.embeds.stream().map((config) -> {
					EmbedBuilder builder = new EmbedBuilder()
							.setTitle(processText(event, config.title), processText(event, config.link))
							.setDescription(processText(event, config.description))
							.setColor(config.color != null ? Color.decode(processText(event, config.color)) : null)
							.setThumbnail(processText(event, config.thumbnail))
							.setAuthor(config.author != null ? processText(event, config.author.name) : null,
									config.author != null ? processText(event, config.author.link) : null,
									config.author != null ? processText(event, config.author.icon) : null)
							.setImage(processText(event, config.image))
							.setTimestamp(config.timestamp != null ? parseTime(processText(event, config.timestamp)) : null)
							.setFooter(config.footer != null ? processText(event, config.footer.text) : null,
									config.footer != null ? processText(event, config.footer.icon) : null);

					for(FieldConfiguration field : config.fields) {
						builder.addField(processText(event, field.name), processText(event, field.value),
								"true".equals(processText(event, field.inline)));
					}

					return builder.build();
				}).collect(Collectors.toList())).build();
	}

	private TemporalAccessor parseTime(String time) {
		return Instant.ofEpochMilli(Long.parseLong(time));
	}

	private String processText(MinecraftToDiscordEvent event, String text) {
		if(text == null) {
			return null;
		}

		return PERCENT_REPLACER.apply(text, event.getPlayer(), (id) -> {
			PlaceholderExpansion expansion = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansion(id);

			if(expansion != null) {
				return expansion;
			}

			return event.getPlaceholders().get(id);
		});
	}

	public static class EmbedConfiguration {

		private String title;
		private String description;
		private String link;
		private String color;
		private String thumbnail;
		private AuthorConfiguration author;
		private List<FieldConfiguration> fields;
		private String image;
		private String timestamp;
		private FooterConfiguration footer;

		public EmbedConfiguration(String title, String description, String link, String color, String thumbnail,
								  AuthorConfiguration author, List<FieldConfiguration> fields, String image, String timestamp,
								  FooterConfiguration footer) {
			this.title = title;
			this.description = description;
			this.link = link;
			this.color = color;
			this.thumbnail = thumbnail;
			this.image = image;
			this.author = author;
			this.fields = fields;
			this.timestamp = timestamp;
			this.footer = footer;
		}

	}

	public static class AuthorConfiguration {

		private String name;
		private String link;
		private String icon;

		public AuthorConfiguration(String name, String link, String icon) {
			this.name = name;
			this.link = link;
			this.icon = icon;
		}

	}

	public static class FieldConfiguration {

		private String name;
		private String value;
		private String inline;

		public FieldConfiguration(String name, String value, String inline) {
			this.name = name;
			this.value = value;
			this.inline = inline;
		}

	}

	public static class FooterConfiguration {

		private String text;
		private String icon;

		public FooterConfiguration(String text, String icon) {
			this.text = text;
			this.icon = icon;
		}

	}

}

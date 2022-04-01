package me.mcblueparrot.bukkicord;

import com.google.common.collect.ImmutableMap;
import me.mcblueparrot.bukkicord.command.BukkicordCommand;
import me.mcblueparrot.bukkicord.config.DiscordMessageConfiguration;
import me.mcblueparrot.bukkicord.event.converter.PlayerAdvancementConverter;
import me.mcblueparrot.bukkicord.event.converter.PlayerDeathConverter;
import me.mcblueparrot.bukkicord.event.converter.PlayerJoinLeaveConverter;
import me.mcblueparrot.bukkicord.event.converter.PlayerMessageConverter;
import me.mcblueparrot.bukkicord.event.impl.MinecraftToDiscordEvent;
import me.mcblueparrot.bukkicord.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class Bukkicord extends JavaPlugin {

	private static Bukkicord instance;

	public static final Component NAME = Component.text("Bukkicord").color(TextColor.fromHexString("#5865F2"));

	private boolean logInError;

	private JDA jda;
	private BukkitAudiences adventure;
	private OOBEManager oobeManager = new OOBEManager(this);

	private Map<String, List<DiscordMessageConfiguration>> discordMessagesByEventId = new HashMap<>();
	private HashMap<String, TextChannel> channelCache = new HashMap<>();

	@Override
	public void onLoad() {
		super.onLoad();
		instance = this;
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		parseConfig();
	}

	private void parseConfig() {
		channelCache.clear();
		discordMessagesByEventId.clear();

		ConfigurationSection minecraftSection = getConfig().getConfigurationSection("minecraft");

		if(minecraftSection != null) {
			for(String event : minecraftSection.getKeys(false)) {
				ConfigurationSection message = minecraftSection.getConfigurationSection(event);

				List<DiscordMessageConfiguration.EmbedConfiguration> embedConfigurations = new ArrayList<>();

				if(message.contains("embeds")) {
					List<?> embeds = message.getList("embeds");

					for (Object embed : embeds) {
						Map<String, ?> embedMap = (Map<String, ?>) embed;

						DiscordMessageConfiguration.AuthorConfiguration author = null;

						if (embedMap.containsKey("author")) {
							Map<String, ?> authorMap = (Map<String, ?>) embedMap.get("author");
							author = new DiscordMessageConfiguration.AuthorConfiguration(
									Utils.toString(authorMap.get("name")), Utils.toString(authorMap.get("link")),
									Utils.toString(authorMap.get("icon")));
						}

						List<DiscordMessageConfiguration.FieldConfiguration> fields = new ArrayList<>();
						if (embedMap.containsKey("fields")) {
							List<?> fieldsList = (List<?>) embedMap.get("fields");

							for (Object field : fieldsList) {
								Map<String, ?> fieldMap = (Map<String, ?>) field;
								fields.add(new DiscordMessageConfiguration.FieldConfiguration(
										Utils.toString(fieldMap.get("key")), Utils.toString(fieldMap.get("value")),
										Utils.toString(fieldMap.get("inline"))));
							}
						}

						DiscordMessageConfiguration.FooterConfiguration footer = null;

						if (embedMap.containsKey("footer")) {
							Map<String, ?> footerMap = (Map<String, ?>) embedMap.get("footer");
							footer = new DiscordMessageConfiguration.FooterConfiguration(
									Utils.toString(footerMap.get("text")), Utils.toString(footerMap.get("icon")));
						}

						embedConfigurations.add(new DiscordMessageConfiguration.EmbedConfiguration(Utils.toString(
								embedMap.get("title")), Utils.toString(embedMap.get("description")),
								Utils.toString(embedMap.get("link")), Utils.toString(embedMap.get("color")),
								Utils.toString(embedMap.get("thumbnail")), author, fields,
								Utils.toString(embedMap.get("image")), Utils.toString(embedMap.get("timestamp")), footer));
					}
				}

				DiscordMessageConfiguration configuration = new DiscordMessageConfiguration(this,
						message.getString("channel"), message.getString("text"), embedConfigurations);

				List<DiscordMessageConfiguration> configurations = discordMessagesByEventId.computeIfAbsent(event, (key) -> new ArrayList<>());
				configurations.add(configuration);
			}
		}
	}

	@Override
	public void onEnable() {
		adventure = BukkitAudiences.create(this);
		saveDefaultConfig();

		String token = getConfig().getString("token");

		if(token != null && !token.equals("0")) {
			try {
				setJDA(createJda(token));
			}
			catch(LoginException error) {
				logInError = true;
				getLogger().log(Level.SEVERE, "Could not log in to bot", error);
			}
			catch(InterruptedException error) {
				return;
			}
		}

		Bukkit.getPluginManager().registerEvents(oobeManager, this);

		Bukkit.getPluginManager().registerEvents(new PlayerMessageConverter(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveConverter(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeathConverter(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerAdvancementConverter(this), this);

		getCommand("bukkicord").setExecutor(new BukkicordCommand(this));
	}

	@Override
	public void onDisable() {
		super.onDisable();

		if(jda != null) {
			jda.shutdown();
		}

		if(oobeManager.getJda() != null) {
			oobeManager.getJda().shutdown();
		}
	}

	public void setJDA(JDA jda) throws InterruptedException {
		this.jda = jda;
		jda.awaitReady();
		logInError = false;
		getLogger().info(getLoginMessage() + "!");
	}

	public JDA getJDA() {
		return jda;
	}

	private String getLoginMessage() {
		return "Logged into " + jda.getSelfUser().getName() + "#" + jda.getSelfUser()
				.getDiscriminator();
	}

	public JDA createJda(String token) throws LoginException {
		return JDABuilder.createLight(token).build();
	}

	public Component getBotStatus() {
		if(getOOBEManager().getStage() != null) {
			return Component.text("Configuring...").color(NamedTextColor.GOLD);
		}
		else if(jda != null) {
			return Component.text(getLoginMessage()).color(NamedTextColor.GREEN);
		}
		else if(logInError) {
			return Component.text("Incorrect Token").color(NamedTextColor.DARK_RED);
		}
		else {
			return Component.text("Not Configured").color(NamedTextColor.RED);
		}
	}

	public BukkitAudiences adventure() {
		return adventure;
	}

	public OOBEManager getOOBEManager() {
		return oobeManager;
	}

	public void sendEventToDiscord(String eventType, MinecraftToDiscordEvent event) {
		List<DiscordMessageConfiguration> messages = discordMessagesByEventId.get(eventType);
		event.setDiscordMessages(() -> {
			if(messages == null) {
				return Collections.emptyMap();
			}

			ImmutableMap.Builder<TextChannel, Message> builder = ImmutableMap.builder();

			for(DiscordMessageConfiguration message : messages) {
				builder.put(message.getChannel(), message.getMessage(event));
			}

			return builder.build();
		});

		Bukkit.getPluginManager().callEvent(event);

		event.getDiscordMessages().forEach((channel, message) -> {
			try {

				channel.sendMessage(message).queue();
			}
			catch(Throwable error) {
				getLogger().log(Level.SEVERE, "Could not send message to #" + channel.getName(), error);
			}
		});
	}

	public static Bukkicord getInstance() {
		return instance;
	}

	public TextChannel getChannel(String channel) {
		return channelCache.computeIfAbsent(channel, (key) -> {
			try {
				int channelId = Integer.parseInt(channel);
				return jda.getTextChannelById(channelId);
			}
			catch(NumberFormatException error) {
				String strippedChannel = channel.startsWith("#") ? channel.substring(1) : channel;

				return jda.getTextChannels().stream()
						.filter((textChannel) -> textChannel.getName().contains(strippedChannel))
						.min(Comparator.comparingInt((textChannel) -> textChannel.getName().length()))
						.orElse(null);
			}
		});
	}

}

package me.mcblueparrot.bukkicord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.security.auth.login.LoginException;

public class OOBEManager extends ListenerAdapter implements Listener {

	private static final Component WAITING_MESSAGE = Component.text("(waiting... type \"exit\" to cancel)");

	private OOBEStage stage;
	private CommandSender activeSender;
	private JDA jda;
	private Bukkicord plugin;
	private String token;

	public OOBEManager(Bukkicord plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(activeSender == null || event.getPlayer() != activeSender) {
			return;
		}

		if(event.getMessage().equalsIgnoreCase("exit")) {
			event.setCancelled(true);
			exitOOBE();
		}
		else if(stage == OOBEStage.TOKEN) {
			event.setCancelled(true);
			useToken(event.getMessage());
		}
	}

	public void useToken(String token) {
		Audience audience = plugin.adventure().sender(activeSender);

		try {
			jda = plugin.createJda(token);
			try {
				jda.awaitReady();
			}
			catch(InterruptedException error) {
				return;
			}

			this.token = token;

			SelfUser user = jda.getSelfUser();
			audience.sendMessage(Component.text("Successfully logged in as " + user.getName() + "#" + user
					.getDiscriminator() + "!").color(NamedTextColor.GREEN).append(Component.newline()));

			String url = "https://discord.com/api/oauth2/authorize?client_id=" + user.getApplicationId() +
					"&permissions=67584&scope=bot";

			stage = OOBEStage.WAITING;

			audience.sendMessage(Component.text("2. Invite").decorate(TextDecoration.BOLD).append(Component
					.newline()));

			if(jda.getGuilds().isEmpty()) {
				audience.sendMessage(Component.text("Next, you need to invite your bot to your server.")
						.append(Component.newline()).append(Component.text("You can create your own link, or use " +
								"the following:")).append(Component.newline()).append(Component.text(url).decorate(
								TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl(url))).append(Component
								.newline()).append(WAITING_MESSAGE));
				jda.addEventListener(this);
			}
			else if(jda.getGuilds().size() == 1) {
				audience.sendMessage(Component.text("The bot already seems to be in a server. This step has been skipped," +
						" as this plugin unfortunately only supports one server at a time."));

				complete();
			}
			else {
				audience.sendMessage(Component.text("The bot is in too many servers. Only 1 is currently" +
						"allowed.").color(NamedTextColor.RED));
				exitOOBE0();
			}
		}
		catch(LoginException error) {
			audience.sendMessage(Component.text("That doesn't look like the right token! Make sure to copy the " +
					"token in the Build-A-Bot section.").color(NamedTextColor.RED));
		}
	}

	private void complete() {
		Audience audience = plugin.adventure().sender(activeSender);
		audience.sendMessage(Component.text("Setup complete! Do not add the bot to any more servers.")
				.color(NamedTextColor.GREEN));

		plugin.reloadConfig();
		plugin.getConfig().set("token", token);
		plugin.saveConfig();
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		if(stage == OOBEStage.WAITING) {
			jda.removeEventListener(this);
			complete();
			exitOOBE0();
		}
	}

	public void enterOOBE(CommandSender sender) {
		Audience audience = plugin.adventure().sender(sender);

		if(activeSender != null) {
			if(sender == activeSender) {
				exitOOBE();
			}
			else {
				audience.sendMessage(Component.text(activeSender.getName() + " is using " +
						"this command.").color(NamedTextColor.RED));
			}
			return;
		}

		if(plugin.getJDA() != null) {
			audience.sendMessage(Component.text("The bot is already configured. Run /bukkicord reset or ")
					.append(Component.text("click here").clickEvent(ClickEvent.runCommand("/bukkicord reset"))
							.decorate(TextDecoration.UNDERLINED)).append(Component
							.text(" to reset.")));
			return;
		}

		activeSender = sender;
		stage = OOBEStage.TOKEN;

		audience.sendMessage(Component.text().append(Bukkicord.NAME.decorate(TextDecoration.BOLD)).append(Component
						.text(" Setup")
				.color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).append(Component.newline()).append(Component.newline())
				.append(Component.text("1. Log In").decorate(TextDecoration.BOLD)).append(Component.newline())
				.append(Component.newline())
				.append(Component.text("You must first allow the plugin to log into your bot."))
				.append(Component.newline()).append(Component.newline())
				.append(Component.text("If you do not have a bot, here are some instructions on how to create " +
						"one:")).append(Component.newline())
				.append(Component.text("1. Go to ")).append(Component
						.text("https://discord.com/developers/").clickEvent(ClickEvent
								.openUrl("https://discord.com/developers/")).decorate(TextDecoration.UNDERLINED)).append(Component.text("."))
				.append(Component.newline())
				.append(Component.text("2. Create a new application with the name of your bot."))
				.append(Component.newline())
				.append(Component.text("3. Select Bot in the settings, and add a new bot."))
				.append(Component.newline()).append(Component.newline())
				.append(Component.text("Enter your bot's token into chat, and press Enter. It won't be shown, " +
						"but you can alternatively run /bukkicord token <token>.")).append(Component.newline())
				.append(WAITING_MESSAGE));
	}

	public void exitOOBE() {
		Audience audience = plugin.adventure().sender(activeSender);
		audience.sendMessage(Component.text("You have exited the setup. Run /bukkicord setup or ").append(
				Component.text("click here").decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.runCommand("/bukkicord setup"))).append(
						Component.text(" to restart.")).color(NamedTextColor.GREEN));

		try {
			plugin.setJDA(jda);
		} catch (InterruptedException error) {
		}

		exitOOBE0();
	}

	private void exitOOBE0() {
		stage = null;
		activeSender = null;
		if(jda != null) {
			jda.shutdown();
		}
	}

	public OOBEStage getStage() {
		return stage;
	}

	public CommandSender getActiveSender() {
		return activeSender;
	}

	public JDA getJda() {
		return jda;
	}

	public enum OOBEStage {
		TOKEN,
		WAITING
	}

}

package me.mcblueparrot.bukkicord.command;

import me.mcblueparrot.bukkicord.Bukkicord;
import me.mcblueparrot.bukkicord.OOBEManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkicordCommand implements CommandExecutor {

	private static final Component NOT_IN_SETUP = Component.text("Not in setup!").color(NamedTextColor.RED);

	private Bukkicord plugin;

	public BukkicordCommand(Bukkicord plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Audience audience = plugin.adventure().sender(sender);

		if(args.length > 0) {
			if(args[0].equals("setup")) {
				plugin.getOOBEManager().enterOOBE(sender);
				return true;
			}
			else if(args[0].equals("token") && args.length > 1) {
				String token = args[1];
				if(plugin.getOOBEManager().getStage() == OOBEManager.OOBEStage.TOKEN) {
					Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getOOBEManager().useToken(token));
				}
				else {
					audience.sendMessage(NOT_IN_SETUP);
				}
				return true;
			}
			else if(args[0].equals("exit")) {
				if(plugin.getOOBEManager().getActiveSender() == sender) {
					plugin.getOOBEManager().exitOOBE();
				}
				else {
					audience.sendMessage(NOT_IN_SETUP);
				}
				return true;
			}
			else if(args[0].equals("status")) {
				audience.sendMessage(Component.text().append(Bukkicord.NAME.append(Component.text(" Status")
						.color(NamedTextColor.GREEN)).decorate(TextDecoration.BOLD)).append(Component.newline())
						.append(Component.text("Bot: ").append(plugin.getBotStatus())));
				return true;
			}
		}

		audience.sendMessage(Component.text("Usage: /bukkicord setup").color(NamedTextColor.RED));
		return true;
	}

}

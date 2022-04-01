package me.mcblueparrot.bukkicord;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for Bukkicord event related placeholders.
 * That's a mouthful!
 */
public abstract class BukkicordPlaceholderExpansion extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "mcblueparrot";
	}

	@Override
	public String getVersion() {
		return Bukkicord.getInstance().getDescription().getVersion();
	}

}

package me.mcblueparrot.bukkicord.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Utils {

	private static final String LANGUAGE_LOCATION = "/assets/minecraft/lang/en_us.json";
	private static Map<String, String> advancementMap;

	public static String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	private static void loadAdvancements() {
		InputStream stream = Bukkit.getServer().getClass().getResourceAsStream(LANGUAGE_LOCATION);
		advancementMap = new HashMap<>();

		if(stream != null) {
			JsonObject obj = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

			for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				if(!(entry.getKey().startsWith("advancements.") && entry.getKey().endsWith(".title"))) {
					continue;
				}

				String key = entry.getKey();
				key = key.substring(13, key.length() - 6);
				key = key.replace(".", "/");
				advancementMap.put(key, entry.getValue().getAsString());
			}
		}
	}

	public static String getAdvancementName(Advancement advancement) {
		if(advancementMap == null) {
			loadAdvancements();
		}

		return advancementMap.getOrDefault(advancement.getKey().getKey(), advancement.getKey().getKey());
	}

}

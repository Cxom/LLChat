package me.cxom.llchat.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.cxom.llchat.LLChat;

public class ConfigManager {

	private static File configf, playersf;
	private static FileConfiguration config, players;
	
	public static void createFiles(){
		
		configf = new File(LLChat.getPlugin().getDataFolder(), "config.yml");
		playersf = new File(LLChat.getPlugin().getDataFolder(), "players.yml");
		
		if(!configf.exists()) {
			configf.getParentFile().mkdirs();
			LLChat.getPlugin().saveResource("config.yml", false);
		}
		if(!playersf.exists()) {
			playersf.getParentFile().mkdirs();
			LLChat.getPlugin().saveResource("players.yml", false);
		}

		config = new YamlConfiguration();
		players = new YamlConfiguration();
		try {
			config.load(configf);
			players.load(playersf);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	public static FileConfiguration getConfig(){
		return config;
	}
	
	public static FileConfiguration getPlayersConfig(){
		return players;
	}

	public static void saveConfig(){
		LLChat.getPlugin().saveConfig();
	}
	
	public static void savePlayersConfig() {
		try {
			players.save(playersf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

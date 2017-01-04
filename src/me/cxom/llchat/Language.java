package me.cxom.llchat;

import org.bukkit.ChatColor;

public enum Language {

	GLOBAL(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "G" + ChatColor.RESET + "", '适'),
	FRENCH("§1F§fR§4A", '退'),
	SPANISH("§6S§4P§6A", '送'),
	GERMAN("§8G§4E§6R", '逃'),
	RUSSIAN("§fR§1U§4S", '逄'),
	PORTUGUESE("§2P§aO§4R", '逅'),
	JAPANESE("§fJ§cP§fN", '逆'),
	ESPERANTO("§fE§3PO", '逇'),
	SWEDISH("§1S§6W§1E", '逈'),
	DANISH("§cD§fA§cN", '选'),
	HEBREW("§1H§fE§1B", '逊'),
	TURKISH("§4T§fU§4R", '逋');
	
	private final String iso;
	private final char flag;
	
	private Language(String iso, char flag){
		this.iso = iso;
		this.flag = flag;
	}
	
	public String getISO(){
		return iso;
	}
	
	public char getFlag(){
		return flag;
	}
	
}

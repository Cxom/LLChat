package me.cxom.llchat;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Language {
	
	GLOBAL(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "G" + ChatColor.RESET + "", '适',
			"http://textures.minecraft.net/texture/30c967926e7abef6ff6bcc41436e215733fd3f85881cf36ed87851711afbdfe"),
	FRENCH("§9F§fR§cA", '退', "http://textures.minecraft.net/texture/dbe7e5873220ca271a87ec2baf8abd77b485ed3118bb72f5b39f4a2bea8f4dbd"),

	SPANISH("§6S§4P§6A", '送', "http://textures.minecraft.net/texture/ed66b2cbe36fb07512e4b6b9ce933bd5cacc04ba0cd43cae79965fce7b5"),
	GERMAN("§8G§4E§6R", '逃', "http://textures.minecraft.net/texture/2aeda329df95540458ad7f9f0f4d871e2957549d6a5bb94c6c68cce5c8995"),
	SLAVIC("§fS§1L§4A", '逄', "http://textures.minecraft.net/texture/d477b532bd23a673c3ce6ce566858d78685e4b9ce2466b642e79330bc9ac4c"),
	PORTUGUESE("§2P§eO§1R", '逅', "http://textures.minecraft.net/texture/b0ada8b1a53f934e32f345a117335ca323cff06ea73d62d8ebb364872a74198"),
	JAPANESE("§fJ§cP§fN", '逆', "http://textures.minecraft.net/texture/361a1c542c453fff8f95f174d50b4674a34fc6249b1c7a02d15b6416e64c1ab"),
	ESPERANTO("§fE§2PO", '逇', "http://textures.minecraft.net/texture/18c9f7e2fdd479d5d699f3ed11ae938bf2c58837b47fe36eb1957c791ecc740"),
	SCANDINAVIAN("§eS§4C§eA", 'c', "http://textures.minecraft.net/texture/af65317b9f2d56b0d4873fcc6ff29e4484b192fae9b8e57ab16c5430d957"),
	//SWEDISH("§9S§eW§9E", '逈', "http://textures.minecraft.net/texture/5c973d5ace721a4d6ecc8c16c8952f78c5539fe1429c325501186aad9310"),
	//DANISH("§4D§fA§4N", '选', "http://textures.minecraft.net/texture/8f5b6f9db8b237b5ec67b58a1efbf8a1a5dcf813d3d869b232847f21c298948"),
	//HEBREW("§9H§fE§9B", '逊', "http://textures.minecraft.net/texture/8f5b6f9db8b237b5ec67b58a1efbf8a1a5dcf813d3d869b232847f21c298948"),
	ITALIAN("§2I§fT§4A", 'c', "http://textures.minecraft.net/texture/25ec622dfe6276192c06bf57ed4ed499e7d9f4e487f14216cd7539b3825c90"),
	TURKISH("§4T§fU§4R", '逋', "http://textures.minecraft.net/texture/b6f34f4f94547712112839d1bbfb99e716a8af766027e5165746b5849d9ec6c");
	
	private final String rawIso;
	private final String iso;
	private final char flag;
	private final ItemStack skull;
	
	private Language(String iso, char flag, String skullUrl){
		this.rawIso = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('§', iso)).toLowerCase();
		this.iso = iso;
		this.flag = flag;
		ItemStack sk = Utils.getSkull(skullUrl);
		ItemMeta im = sk.getItemMeta();
		im.setDisplayName(getName());
		sk.setItemMeta(im);
		this.skull = sk;
	}
	
	public String getRawISO(){
		return rawIso;
	}
	
	public String getISO(){
		return iso;
	}
	
	public char getFlag(){
		return flag;
	}
	
	public ItemStack getSkull(){
		return skull;
	}
	
	public String getName(){
		return StringUtils.capitalize(name().toLowerCase().replaceAll("_", " "));
	}
	
}

package me.cxom.llchat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatChannel{
	
	public static final ChatChannel getGlobal(){
		return LLChat.getChatChannel("GLOBAL");
	}
	
	private final String name;
	private final Language lang;
	private final Set<LLChatPlayer> subscribers = new HashSet<>(); 
	
	public ChatChannel(String name, Language lang){
		this.name = name;
		this.lang = lang;
	}
	
	@Override
	public boolean equals(Object o){
		if(! (o instanceof ChatChannel)) return false;
		ChatChannel cc = (ChatChannel) o;
		return name.equals(cc.getName()) && lang == cc.getLanguage(); 
	}

	public String getName(){
		return name;
	}
	
	public Language getLanguage(){
		return lang;
	}
	
	public void addMember(LLChatPlayer llp){
		subscribers.add(llp);
	}
	
	public void removeMember(LLChatPlayer llp){
		subscribers.remove(llp);
	}
	
	public void sendMessage(String message, String sender){
		for(LLChatPlayer llp : subscribers){
			Player player = llp.getPlayer();
			String color = this.equals(llp.getMainChatChannel()) ? ChatColor.WHITE + "" : ChatColor.GRAY + "";
			String msg = String.format("%s<%s%s: %s%s> %s", color, 
					                                     llp.hasResourcePack() ? "§r" + lang.getFlag() : lang.getISO() + " ",
					                                     color,
													     sender,
													     color,
													     message);
			player.sendMessage(msg);
		}
	}
	
}

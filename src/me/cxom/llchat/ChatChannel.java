package me.cxom.llchat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
		System.out.println(ChatColor.stripColor("<" + lang.getISO() + ": " + sender + "> ") + message);
		for(LLChatPlayer llp : subscribers){
			Player player = llp.getPlayer();
			String color = this.equals(llp.getMainChatChannel()) ? ChatColor.WHITE + "" : ChatColor.GRAY + "";
			TextComponent start = new TextComponent(TextComponent.fromLegacyText(color + "<"));
			TextComponent channel = new TextComponent(TextComponent.fromLegacyText(llp.hasResourcePack() ? "§r" + lang.getFlag() : lang.getISO() + " "));
			channel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/channel main " + lang.getName()));
			channel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join " + lang.getName() + "?").italic(true).create()));
			String msg = String.format("%s: %s%s> %s", color,
													   sender,
													   color,
													   message);
			TextComponent rest = new TextComponent(TextComponent.fromLegacyText(msg));
			start.addExtra(channel);
			start.addExtra(rest);
			player.spigot().sendMessage(start);
		}
	}
	
}

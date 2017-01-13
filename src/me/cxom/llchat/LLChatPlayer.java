package me.cxom.llchat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LLChatPlayer {

	private final UUID uuid;
	private ChatChannel main;
	private List<ChatChannel> channels = new ArrayList<>();
	private boolean hasResourcePack = false;
	
	public LLChatPlayer(UUID uuid){
		this.uuid = uuid;
		addChatChannel(ChatChannel.getGlobal());
		setMainChatChannel(ChatChannel.getGlobal());
	}
	
	public UUID getUniqueId(){
		return uuid;
	}
	
	public Player getPlayer(){
		return Bukkit.getPlayer(uuid);
	}
	
	public boolean hasResourcePack(){
		return hasResourcePack;
	}
	
	public void setHasResourcePack(boolean b){
		hasResourcePack = b;
	}
	
	public void addChatChannel(ChatChannel cc){
		cc.addMember(this);
		channels.add(cc);
		getPlayer().sendMessage(ChatColor.GREEN + "You have joined the " + cc.getName() + " channel.");
	}
	
	public boolean isInChannel(ChatChannel cc){
		return channels.contains(cc);
	}
	
	public void removeChatChannel(ChatChannel cc){
		cc.removeMember(this);
		channels.remove(cc);
		getPlayer().sendMessage(ChatColor.GREEN + "You have left the " + cc.getName() + " channel.");
	}
	
	public void setMainChatChannel(ChatChannel main){
		if(!isInChannel(main)){
			addChatChannel(main);
		}
		this.main = main;
		getPlayer().sendMessage(ChatColor.GREEN + "The " + main.getName() + " channel has been set as your speaking channel.");
	}
	
	public ChatChannel getMainChatChannel(){
		return main;
	}
	
	public List<ChatChannel> getChatChannels(){
		return channels;
	}
	
	public void sendMessage(ChatChannel channel, String message){
		Player player = Bukkit.getPlayer(uuid); 
		String color = main.equals(channel) ? ChatColor.WHITE + "" : ChatColor.GRAY + "";
		message = String.format("%s<%s%s: %s%s> %s", color, 
				                                    hasResourcePack ? "&r" + channel.getLanguage().getFlag() : channel.getLanguage().getISO() + " ",
				                                    color,
												player.getName(),
				                                color,
												message);
		player.sendMessage(message);
	}
	
	public void remove(){
		for(ChatChannel cc : channels){
			cc.removeMember(this);
		}
	}
	
}

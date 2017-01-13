package me.cxom.llchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LLChat extends JavaPlugin implements Listener{
	
	private static Map<UUID, LLChatPlayer> players = new HashMap<>();
	private static Map<String, ChatChannel> channels = new LinkedHashMap<>();
	
	public static ChatChannel getChatChannel(String name){
		return channels.get(name.toUpperCase());
	}
	
	public static List<ChatChannel> getChannels(){
		return new ArrayList<>(channels.values());
	}
	
	public static LLChatPlayer getPlayer(UUID uuid){
		return players.get(uuid);
	}
	
	@Override
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(new ChatChannelGUI(), this);
		for(Language l : Language.values())
			channels.put(l.name(), new ChatChannel(l.getName(), l));
		for(Player p : Bukkit.getOnlinePlayers()){
			UUID uuid = p.getUniqueId();
			players.put(uuid, new LLChatPlayer(uuid));
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (label.equalsIgnoreCase("lltest")){
			channels.get("FRENCH").sendMessage(args[0], "Console");
			return true;
		}else if (label.equalsIgnoreCase("lltest2")){
			channels.get("SPANISH").sendMessage(args[0], "Console");
			return true;
		}
		
		if( ! (sender instanceof Player)) return true;
		Player player = (Player) sender;
		LLChatPlayer llp = players.get(player.getUniqueId());
		if (label.equalsIgnoreCase("channel") || label.equalsIgnoreCase("ch") || label.equalsIgnoreCase("chat")){
			if (args.length < 1){
				//player.sendMessage(ChatColor.RED + "/channel <add|remove|main|list|status>");
				ChatChannelGUI.open(llp);
				return true;
			}
			
			switch (args[0]) {
			case "help":
				player.sendMessage(ChatColor.GRAY + "---------LLChat Help---------");
				player.sendMessage(" " + ChatColor.GREEN + "/channel help" + ChatColor.GRAY + " for a list of commands and what they do.");
				player.sendMessage(" " + ChatColor.GREEN + "/channel list" + ChatColor.GRAY + " for a list ofr available channels.");
				player.sendMessage(" " + ChatColor.GREEN + "/channel status" + ChatColor.GRAY + " for a list of channels your subscribed to.");
				player.sendMessage(" " + ChatColor.GREEN + "/channel add <channel>" + ChatColor.GRAY + " to subscribe to a new channel.");
				player.sendMessage(" " + ChatColor.GREEN + "/channel remove <channel>" + ChatColor.GRAY + " to unsubscribe from a channel.");
				player.sendMessage(" " + ChatColor.GREEN + "/channel main <channel>" + ChatColor.GRAY + " to set which channel you're");
				player.sendMessage("    " + ChatColor.GRAY + "speaking in.");
				player.sendMessage(ChatColor.GRAY + "-----------------------------");
				return true;
			case "list":
				player.sendMessage(ChatColor.GRAY + "-------All Chat Channels---------");
				for (ChatChannel cc : channels.values())  
					player.sendMessage("    " + ChatColor.YELLOW + cc.getName() + "  §8(" + cc.getLanguage().getISO() + "§8)");
				player.sendMessage(ChatColor.GRAY + "-------------------------------");
				return true;
			case "status":
				ChatChannel main = llp.getMainChatChannel();
				player.sendMessage(ChatColor.GRAY + "-------Your Chat Channels-------");
				player.sendMessage("    " + ChatColor.DARK_AQUA + main.getName() + ChatColor.DARK_GRAY + " - Active");
				for(ChatChannel cc : llp.getChatChannels()){
					if (cc.equals(main)) continue;
					player.sendMessage("    " + ChatColor.AQUA + cc.getName());
				}
				player.sendMessage(ChatColor.GRAY + "-------------------------------");
				return true;
			default:
				break;
			}
			
			if (args.length < 2){
				player.sendMessage(ChatColor.RED + "/channel <add|remove|main> <channel>");
				return true;
			}
			ChatChannel cc = channels.get(args[1].toUpperCase());
			if (cc == null){
				player.sendMessage(ChatColor.RED + "There is no channel with that name!");
				player.sendMessage(ChatColor.GRAY + "You can do \"/channel list\" for a list of channels.");
				return true;
			}
			switch (args[0]) {
			case "add":
			case "join":
				if(llp.isInChannel(cc)){
					player.sendMessage(ChatColor.RED + "You are already in that channel!");
					break;
				}
				llp.addChatChannel(cc);
				break;
			case "remove":
			case "leave":
				if(!llp.isInChannel(cc)){
					player.sendMessage(ChatColor.GRAY + "You aren't in that channel!");
					break;
				}
				llp.removeChatChannel(cc);
				if(llp.getMainChatChannel().equals(cc)){
					player.sendMessage(ChatColor.RED + "Err, that was your speaking channel. Attempting to default to one of your other channels.");
					if (llp.getChatChannels().isEmpty()){
						player.sendMessage(ChatColor.RED + " . . . Seems you have no other active channels. Defaulting you to Global chat.");
						Bukkit.dispatchCommand(player, "channel main global");
						return true;
					}
					ChatChannel cc2 = llp.getChatChannels().get(0);
					llp.setMainChatChannel(cc2);
				}
				break;
			case "main":
			case "active":
			case "set":
				if(llp.getMainChatChannel().equals(cc)){
					player.sendMessage(ChatColor.RED + "That is already your speaking channel!");
					break;
				}
				llp.setMainChatChannel(cc);
				break;
			default:
				player.sendMessage(ChatColor.RED + "That subcommand was not recognized.");
				player.sendMessage(ChatColor.RED + "/channel <add|remove|main|list|status|help>");
			}
		}
		
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		players.put(uuid, new LLChatPlayer(uuid));
	}
	
	@EventHandler
	public void onResourcepackStatusEvent(PlayerResourcePackStatusEvent e){
		System.out.println("Hey, this actually got triggered!");
		if(e.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED){
			players.get(e.getPlayer().getUniqueId()).setHasResourcePack(true);
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){
		LLChatPlayer llp = players.get(e.getPlayer().getUniqueId());
		llp.remove();
		players.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		LLChatPlayer llp = players.get(e.getPlayer().getUniqueId());
		llp.getMainChatChannel().sendMessage(e.getMessage(), e.getPlayer().getDisplayName());
		e.setCancelled(true);
	}
	
}

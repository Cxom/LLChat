package me.cxom.llchat;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class ChatChannelGUI implements Listener{

	public static Inventory channelGUI = Bukkit.createInventory(null, 54, "LLChat Channels");	
	
	private static final ItemStack main = Utils.getSkull("http://textures.minecraft.net/texture/ff373b5157eb24c6db4827b585b195e2513a7599784362c739c3e86fefbd18a");
			//"http://textures.minecraft.net/texture/eeca87401a9bf37c8b55cac4a45c911b843dbd9c0c96cbe8291335c5069043"); - DarkPA
			//"http://textures.minecraft.net/texture/f21f41c54b9b681f2c3ea7d8c71d3cac8fb82f199905a6725d5701de9bdd98" Red
			//"http://textures.minecraft.net/texture/5aa54d742492989f739deb7222a23bcfcf85e6c782fe2ce0cdeee0f3f2b0eb" Computer
	private static final ItemStack active = new ItemStack(Material.GOLD_BLOCK);
	private static final ItemStack joinAll = new ItemStack(Material.EMERALD_BLOCK);
	private static final ItemStack exit = new ItemStack(Material.REDSTONE_BLOCK);
	
	private static final ItemStack help = Utils.getSkull("http://textures.minecraft.net/texture/5359d91277242fc01c309accb87b533f1929be176ecba2cde63bf635e05e699b"); //ICEQ PIXELFIX
			//"http://textures.minecraft.net/texture/974679ac1c5dda815f717546a318fe1276882773b18ea485612c2d61ab85"); - Log
			//"http://textures.minecraft.net/texture/65effb68b0e6d2fe2a5669a682927e318dac8332eff2a950f170fbe465f1f9" - Bigger Q Log
			//"http://textures.minecraft.net/texture/5163dafac1d91a8c91db576caac784336791a6e18d8f7f62778fc47bf146b6" - Regular Log
	
	static{
		ItemMeta mm = main.getItemMeta();
		mm.setDisplayName("Speak here?");
		main.setItemMeta(mm);
		
		active.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		
		ItemMeta jam = joinAll.getItemMeta();
		jam.setDisplayName(ChatColor.ITALIC + "Join all channels?");
		joinAll.setItemMeta(jam);
		
		ItemMeta im = exit.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Exit");
		exit.setItemMeta(im);
		
		ItemMeta hm = help.getItemMeta();
		hm.setDisplayName(ChatColor.LIGHT_PURPLE + "Need help?");
		hm.setLore(Arrays.asList("Click here!",
								 "§o§8(displayed in chat)"));
		help.setItemMeta(hm);
	}
	
	
	public static void open(LLChatPlayer llp){
		open(llp, 1);
	}
	
	private static ItemStack setName(ItemStack is, String name){
		is = is.clone();
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}
	
	private static void open(LLChatPlayer llp, int page){
		Inventory inv = Bukkit.createInventory(null, 54, "LLChat Channels - Page " + page);
		int base = (page - 1) * 9;
		List<ChatChannel> channels = LLChat.getChannels();
		for (int k = 0; k < 9; k++){
			if (base + k >= channels.size()) break;
			ChatChannel cc = channels.get(base + k);
			inv.setItem(k + 9, cc.getLanguage().getSkull());
			inv.setItem(k + 27, main);
			ItemStack is;
			if(cc.equals(llp.getMainChatChannel())){
				is = active;
				is = setName(is, ChatColor.ITALIC + "Speaking in " + cc.getName());
				inv.setItem(k + 27, is);
			}else if(llp.isInChannel(cc)){
				is = new ItemStack(Material.STAINED_CLAY, 1 , (short) 13);
				is = setName(is, ChatColor.ITALIC + "Leave " + cc.getName());
			}else{
				is = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
				is = setName(is, ChatColor.ITALIC + "Join " + cc.getName());
			}
			inv.setItem(k + 18, is);
			
		}
		
		if(page != 1){
			ItemStack previous = setName(new ItemStack(Material.STAINED_GLASS_PANE), "Previous Page");
			inv.setItem(48, previous);
		}
		if(page != Math.ceil(channels.size() / 9d)){
			ItemStack next = setName(new ItemStack(Material.STAINED_GLASS_PANE), "Next Page");
			inv.setItem(50, next);
		}
		
		inv.setItem(46, help);
		inv.setItem(53, joinAll);
		
		inv.setItem(49, exit);
		llp.getPlayer().openInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		if (e.getCurrentItem() == null) return;
		if (!e.getCurrentItem().hasItemMeta()) return;
		if (e.getInventory().getName().startsWith("LLChat Channels")){
			ItemStack is = e.getCurrentItem();
			LLChatPlayer llp = LLChat.getPlayer(e.getWhoClicked().getUniqueId());
			switch (e.getCurrentItem().getType()){
			case STAINED_CLAY:
				if(is.getData().getData() == 14){
					ChatChannel cc = LLChat.getChatChannel(e.getInventory().getItem(e.getSlot() - 9).getItemMeta().getDisplayName());
					llp.addChatChannel(cc);
					
					is = new ItemStack(Material.STAINED_CLAY, 1 , (short) 13);
					is = setName(is, ChatColor.ITALIC + "Leave " + cc.getName());
					e.getInventory().setItem(e.getSlot(), is);
				}else if(is.getData().getData() == 13){
					ChatChannel cc = LLChat.getChatChannel(e.getInventory().getItem(e.getSlot() - 9).getItemMeta().getDisplayName());
					llp.removeChatChannel(cc);
					
					is = new ItemStack(Material.STAINED_CLAY, 1 , (short) 14);
					is = setName(is, ChatColor.ITALIC + "Join " + cc.getName());
					e.getInventory().setItem(e.getSlot(), is);
				}
				break;
			case SKULL_ITEM:
				String name = ChatColor.stripColor(is.getItemMeta().getDisplayName()); 
				if(name.equals("Speak here?")){
					
					ChatChannel old = llp.getMainChatChannel();
					int i = old.getLanguage().ordinal();
					int page = Integer.parseInt(e.getInventory().getName().substring(23)) - 1;
					if(page * 9 <= i && i < page * 9 + 9){
						ItemStack green = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
						green = setName(green, ChatColor.ITALIC + "Leave " + old.getName());
						e.getInventory().setItem(i % 9 + 18, green);
						e.getInventory().setItem(i % 9 + 27, main);
					}
					
					String channel = e.getInventory().getItem(e.getSlot() - 18).getItemMeta().getDisplayName();
					ItemStack active = setName(ChatChannelGUI.active, ChatColor.ITALIC + "Speaking in " + channel);
					e.getInventory().setItem(e.getSlot(), active);
					e.getInventory().setItem(e.getSlot() - 9, active);
					
					llp.setMainChatChannel(LLChat.getChatChannel(channel));
				}else if (name.equals("Need help?")){
					Player player = (Player) e.getWhoClicked();
					player.sendMessage(ChatColor.DARK_GRAY + "---------LLChat Guide---------");
					player.sendMessage(ChatColor.YELLOW + "Congrats, you've figured out /ch! Here's a proper guide to getting started:");
					player.sendMessage(ChatColor.GRAY + "-" + ChatColor.AQUA + "When you open the menu, click on a §4red clay block§b to §4join§b a channel or a §2green clay block§b to §2leave§b one! "
													  	 					 + "You can also click the §aemerald§b block to §ajoin all§b the channels.");
					player.sendMessage(ChatColor.GRAY + "-" + ChatColor.AQUA + "The §egold blocks§b show you what channel you're §espeaking in§b. Click on a purple §d\"SPEAK\"§b head to §dchange§b that!");
					player.sendMessage(ChatColor.GRAY + "-" + ChatColor.AQUA + "Chat from the channel you're §fspeaking in§b will show up in §fwhite§b. All §7other channels§b will show up in §7gray§b.");
					player.sendMessage(ChatColor.BLUE + " You can also switch speaking channels by clicking on the channel prefixes in chat. Try it!");
					 
					player.sendMessage(ChatColor.GRAY + "-" + ChatColor.AQUA + "Whatever you choose, your preferences are saved when you log out "
																			 + "(you'll be put in §3Global§b when you log in no matter what though!)");
				    player.sendMessage(ChatColor.GRAY + "-" + ChatColor.AQUA + "Want to manage your channels through commands?. Run \"/ch help\" for a guide.");
				    player.sendMessage(ChatColor.DARK_GRAY + "------------------------------");
				    player.closeInventory();
				}
				break;
			case STAINED_GLASS_PANE:
				int page = Integer.parseInt(e.getInventory().getName().substring(23));
				if(is.getItemMeta().getDisplayName().equals("Next Page")){
					open(llp, page + 1);
				}else{
					open(llp, page - 1);
				}
				//llp.getPlayer().sendMessage(ChatColor.RED + "Err, something went wrong (NFE). Yell at Cxom.");
				break;
			case EMERALD_BLOCK:
				if (!e.getCurrentItem().getItemMeta().getDisplayName().equals("§oJoin all channels?")) return;
				page = Integer.parseInt(e.getInventory().getName().substring(23)) - 1;
				for(ChatChannel cc : LLChat.getChannels()){
					if(!llp.isInChannel(cc)){
						llp.addChatChannel(cc);
						int i = cc.getLanguage().ordinal();
						if(page * 9 <= i && i < page * 9 + 9){
							ItemStack green = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
							green = setName(green, ChatColor.ITALIC + "Leave " + cc.getName());
							e.getInventory().setItem(i % 9 + 18, green);
						}
					}
				}
				break;
			case REDSTONE_BLOCK:
				e.getWhoClicked().closeInventory();
				break;
			default:
				break;
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMenuDrag(InventoryDragEvent e){
		if(e.getInventory().getName().startsWith("LLChat Channels"))
			e.setCancelled(true);
	}
	
}

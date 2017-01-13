package me.cxom.llchat;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
			//+ "http://textures.minecraft.net/texture/eeca87401a9bf37c8b55cac4a45c911b843dbd9c0c96cbe8291335c5069043"); - DarkPA
			//"http://textures.minecraft.net/texture/f21f41c54b9b681f2c3ea7d8c71d3cac8fb82f199905a6725d5701de9bdd98" Red
			//"http://textures.minecraft.net/texture/5aa54d742492989f739deb7222a23bcfcf85e6c782fe2ce0cdeee0f3f2b0eb" Computer
	private static final ItemStack active = new ItemStack(Material.GOLD_BLOCK);
	private static final ItemStack joinAll = new ItemStack(Material.EMERALD_BLOCK);
	private static final ItemStack exit = new ItemStack(Material.REDSTONE_BLOCK);
	
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
				if(ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals("Speak here?")){
					
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

package com.electro2560.dev.QuestionBlocks.Events;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.electro2560.dev.QuestionBlocks.Main;
import com.electro2560.dev.QuestionBlocks.Perms;
import com.electro2560.dev.QuestionBlocks.Utils.QBlock;
import com.electro2560.dev.QuestionBlocks.Utils.Utils;

public class BlockClickEvent implements Listener{
		
		@SuppressWarnings("deprecation")
		@EventHandler(ignoreCancelled = true)
		public void onBlockClick(PlayerInteractEvent event){	
			Player p = event.getPlayer();
			
			//Listen for clicks of rewards
			if(event.getClickedBlock() == null) return;

			//In 1.10, this will prevent the event from being called twice
			if(p.getItemInHand().equals(event.getItem())) return;
			
			if(p.hasPermission(Perms.adminPerm) && p.getGameMode() == GameMode.CREATIVE && Main.selectionItem == p.getItemInHand().getTypeId()){
				//Set selection
				Block b = event.getClickedBlock();
				Main.selections.put(p.getUniqueId().toString(), Utils.locToString(b.getLocation()));
				p.sendMessage(Main.prefix + ChatColor.GREEN + "You selected block " +  ChatColor.GRAY + b.getType() + ChatColor.GREEN + " at location " + ChatColor.GRAY + "(§a" + b.getLocation().getWorld().getName() + "§7,§a " + b.getLocation().getBlockX() + "§7,§a " + b.getLocation().getBlockY() + "§7,§a " + b.getLocation().getBlockZ() + "§7)§a.");
				event.setCancelled(true);
				return;
			}
				
				String loc = Utils.locToString(event.getClickedBlock().getLocation());
				
				//Contains rewards
				if(!Main.qBlocks.isEmpty() && Main.qBlocks.containsKey(loc)){
					//There is a reward
					//Execute reward
					QBlock r = Main.qBlocks.get(loc);

					event.setCancelled(true);
					//Check delay
						if(Utils.checkDelay(p, r)){
							//Can receive reward.
							//Add reward items to inventory
							int dropped = 0;
							for (ItemStack i : r.getRewardItems()) {
								if(r.getType().equalsIgnoreCase("inv")){
									if(p.getInventory().firstEmpty() != -1){
										//Inventory has space.
										p.getInventory().addItem(i);
									}else{
										//Drop item on ground.
										p.getWorld().dropItem(p.getLocation(), i);
										dropped++;
									}
								}else p.getWorld().dropItem(Utils.stringToLoc(r.getLocation()), i);
							}

							if(dropped == 1) p.sendMessage(Main.errPrefix + ChatColor.RED + "Full inventory. Dropped 1 item.");
							else if(dropped != 0) p.sendMessage(Main.errPrefix + ChatColor.RED + "Full inventory. Dropped " + dropped + " items.");
							
							p.sendMessage(Main.prefix + ChatColor.GREEN + "You have found this reward §7" + (r.getReceived().get(p.getUniqueId().toString()) == null ? "0" : r.getReceived().get(p.getUniqueId().toString())) + "§a time(s) before!");
							if(r.getReceived().containsKey(p.getUniqueId().toString())) r.getReceived().put(p.getUniqueId().toString(), r.getReceived().get(p.getUniqueId().toString()) + 1);
							else r.getReceived().put(p.getUniqueId().toString(), 1);
						}
					
				}
		}
}

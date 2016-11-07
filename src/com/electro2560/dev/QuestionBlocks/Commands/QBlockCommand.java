package com.electro2560.dev.QuestionBlocks.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.electro2560.dev.QuestionBlocks.Main;
import com.electro2560.dev.QuestionBlocks.Perms;
import com.electro2560.dev.QuestionBlocks.Utils.QBlock;
import com.electro2560.dev.QuestionBlocks.Utils.Utils;

@SuppressWarnings({ "deprecation"})
public class QBlockCommand implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(sender.hasPermission(Perms.adminPerm)){
					if(args.length == 0){
						p.sendMessage(Main.help);
					}else{
						if(args.length >= 1){
							switch (args[0].toLowerCase()) {
							case "create":
								//Arguments: create <name> <item> <type> <time>
								if(args.length >= 5){
									
									if(Main.selections.get(p.getUniqueId().toString()) == null){
										p.sendMessage(ChatColor.RED + "Error: You do not have a block selected!");
										return false;
									}
									
									String name = args[1];
									String item = args[2];
									String type = args[3].toLowerCase();
									String timeRaw = args[4];
									
									int time;
									
									try {
										time = Integer.parseInt(timeRaw);
									} catch (Exception e) {
										p.sendMessage(ChatColor.RED + "Error: " + timeRaw + " is not a valid number for a time!");
										return false;
									}
									
									for(QBlock q : Main.qBlocks.values()){
										if(ChatColor.stripColor(q.getName()).equals(name)){
											p.sendMessage(ChatColor.RED + "Error: A question block by the name " + name + " already exists! Please use /qblock set to edit it or /qblock delete to delete it.");
											return false;
										}
									}
									
									Material itemType = null;
									
									try {
										itemType = Material.matchMaterial(item);
									} catch (Exception e) {
										itemType = null;
									}finally {
										if(itemType == null){
											p.sendMessage(ChatColor.RED + "Error: The item type " + item + " couldn't be found! Double check its spelling.");
											return false;
										}
									}
									
									if(!type.equals("inv") && !type.equals("pop")){
										p.sendMessage(ChatColor.RED + "Error: Invalid type! Must be either inv or pop. You specified " + type + ".");
										return false;
									}
									
									if(Main.qBlocks.containsKey(Main.selections.get(p.getUniqueId().toString()))){
										p.sendMessage(ChatColor.RED + "Error: This block has already been setup as a question block. Please use the /qblock set commands to change it or use /qblock delete to delete it.");
										return false;
									}
									
									//create a Qblock with the data now. check if block already has a question block created at its loc
									Main.qBlocks.put(Main.selections.get(p.getUniqueId().toString()), new QBlock(name, time, new ArrayList<ItemStack>(Arrays.asList(new ItemStack(itemType, 1))), Main.selections.get(p.getUniqueId().toString()), null, null, type));
									
									Location l = Utils.stringToLoc(Main.selections.get(p.getUniqueId().toString()))	;
									
									//Let's set the skull state
									BlockState state = l.getBlock().getState();

									if(state instanceof Skull){
										Utils.setSkullWithNonPlayerProfile(Main.getInstance().getConfig().getString("skinURL", "http://textures.minecraft.net/texture/b3b710b08b523bba7efba07c629ba0895ad61126d26c86beb3845603a97426c"), true, l.getBlock());
									}
									
									p.sendMessage(Main.prefix + "§aCreated " + name + " with an item of " + item + ", a delay of " + time + " seconds and type of " + type + " at the location " + ChatColor.GRAY + "(§a" + l.getWorld().getName() + "§7,§a " + l.getBlockX() + "§7,§a " + l.getBlockY() + "§7,§a " + l.getBlockZ() + "§7)§a.");									
								}else{
									//Missing args...
									p.sendMessage(ChatColor.RED + "Error: Missing args! Please use /qblock create <name> <item> <type> <time>");
								}
								
								break;
							case "set":
								if(args.length >= 2){
									if(Main.selections.containsKey(p.getUniqueId().toString())){
										String selectedBlock = Main.selections.get(p.getUniqueId().toString());
										
										if(!Main.qBlocks.containsKey(selectedBlock)){
											p.sendMessage(ChatColor.RED + "Error: The selected block is not a question block! Use /qblock create");
											return false;
										}
										
										QBlock r = Main.qBlocks.get(selectedBlock);
										
										switch (args[1].toLowerCase()){
										case "item":
											if(args[1] == null){
												p.sendMessage(ChatColor.RED + "Error: You must specify a item type.");
												return false;
											}
											if(Material.matchMaterial(args[1]) == null){
												p.sendMessage(ChatColor.RED + "Error: " + args[1] + " is not a valid item type!");
												return false;
											}
											
											r.setRewardItems((ArrayList<ItemStack>) Arrays.asList(new ItemStack(Material.matchMaterial(args[1]), 1)));
											p.sendMessage(Main.prefix  + ChatColor.GREEN + "Set items of selected reward to your clipboard.");
											break;
										case "time":
											if(args.length >= 3){
												if(NumberUtils.isNumber(args[2])){
													r.setDelay(Integer.parseInt(args[2]));
													p.sendMessage(Main.prefix + ChatColor.GREEN + "Set delay to " + args[2] + " which is equivlent to " + Utils.timeToMinSec(Long.parseLong(args[2]) * 1000) + ".");
												}else p.sendMessage(ChatColor.RED + "Error: \"" + args[2] + "\" is not a number!");
											}else p.sendMessage(ChatColor.RED + "Error: You must enter a time in seconds! Example: '300' = 5 minutes.");
											break;
										case "type":
											if(args.length >= 3){
												String type = args[2].toLowerCase();
												
												if(!type.equalsIgnoreCase("inv") && !type.equalsIgnoreCase("pop")){
													p.sendMessage(ChatColor.RED + "Error: You must enter a type of inv or pop.");
													return false;
												}
												
												r.setType(type);
												p.sendMessage(Main.prefix + ChatColor.GREEN + "Set type to " + type + ".");
											}else p.sendMessage(ChatColor.RED + "Error: You must a type of either inv or pop!");
											break;
										default:
											p.sendMessage(ChatColor.RED + "Error: Unknow option! Use /reward set help for help");
											break;
										}	
									}else{
										p.sendMessage(ChatColor.RED + "Error: You must select a block to edit. Use the block " + Main.selectionItem + " to do this.");
									}
								}else p.sendMessage(Main.help);
								break;
							case "delete":
								if(Main.selections.containsKey(p.getUniqueId().toString())){
									Main.getInstance().getConfig().set("items." + Main.selections.get(p.getUniqueId().toString()), null);
									if(Main.qBlocks.containsKey(Main.selections.get(p.getUniqueId().toString()))){
										Main.qBlocks.remove(Main.selections.get(p.getUniqueId().toString()));
									}
									p.sendMessage(Main.prefix  +ChatColor.GREEN + "Any question blocks at that location have been removed along with any associated data!");
								}else p.sendMessage(ChatColor.RED + "Error: You do not have a block selected!");
								break;
							case "list":
								//Send the player a list of all rewards
								String list = ChatColor.RED + "None";
								for(Map.Entry<String, QBlock> entry : Main.qBlocks.entrySet()){
									String[] l = entry.getKey().split(":");
									QBlock r = entry.getValue();
									String loc = "(" + l[0] + ", " + l[1] + ", " + l[2] + ", " + l[3] + ")";
									if(list.equals(ChatColor.RED + "None")) list = "";
									list += r.getName() + ChatColor.RESET + "" + ChatColor.AQUA + " - " + ChatColor.GREEN + loc + "\n";
								}
								p.sendMessage(list);
								break;
							case "info":
								if(Main.selections.containsKey(p.getUniqueId().toString())){
									if(Main.qBlocks.containsKey(Main.selections.get(p.getUniqueId().toString()))){
										QBlock r = Main.qBlocks.get(Main.selections.get(p.getUniqueId().toString()));
										p.sendMessage(ChatColor.AQUA + "Name: " + ChatColor.RESET + r.getName());
										p.sendMessage(ChatColor.AQUA + "Delay: " + ChatColor.GREEN + r.getDelay());
										p.sendMessage(ChatColor.AQUA + "Received " + ChatColor.GREEN + r.getReceived().size() + ChatColor.AQUA + " times!");
										p.sendMessage(ChatColor.AQUA + "Item: " + ChatColor.GREEN + r.getRewardItems().get(0).getType().toString().toLowerCase());
										p.sendMessage(ChatColor.AQUA + "Type: " + ChatColor.GREEN + r.getType());
									}else p.sendMessage(ChatColor.RED + "Error: The block you have selected is not a question block!");
								}else p.sendMessage(ChatColor.RED + "Error: You do not have any question block selected!");
								break;
							case "updateskin":
								if(Main.selections.containsKey(p.getUniqueId().toString())){
									if(Main.qBlocks.containsKey(Main.selections.get(p.getUniqueId().toString()))){
										Block b = Utils.stringToLoc(Main.selections.get(p.getUniqueId().toString())).getBlock();

										//Let's set the skull state
										BlockState state = b.getState();

										if(state instanceof Skull){
											Utils.setSkullWithNonPlayerProfile(Main.getInstance().getConfig().getString("skinURL", "http://textures.minecraft.net/texture/b3b710b08b523bba7efba07c629ba0895ad61126d26c86beb3845603a97426c"), true, b);
											
											p.sendMessage(ChatColor.GREEN + "Skin updated! You may need to relog to view the changes.");
										}else{
											p.sendMessage(ChatColor.RED + "Error: That block is not a skull!");
											break;
										}
										
										
									}else p.sendMessage(ChatColor.RED + "Error: The block you have selected is not a question block!");
								}else p.sendMessage(ChatColor.RED + "Error: You do not have any question block selected!");
								break;
							case "help":
								p.sendMessage(Main.help);
								break;
							default:
								p.sendMessage(Main.help);
								break;
							}
						}else p.sendMessage(Main.help);
						
					}
				}else sender.sendMessage(ChatColor.RED + "Error: You do not have permission to use this command!");
			}else{
				//Console commands: None
				sender.sendMessage(ChatColor.RED + "Error: Console is currently not supported!");
			}
		
		return false;
	}
}

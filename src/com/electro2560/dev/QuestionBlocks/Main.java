package com.electro2560.dev.QuestionBlocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.electro2560.dev.QuestionBlocks.Commands.QBlockCommand;
import com.electro2560.dev.QuestionBlocks.Events.BlockClickEvent;
import com.electro2560.dev.QuestionBlocks.Utils.QBlock;

@SuppressWarnings("deprecation")
public class Main extends JavaPlugin{
	
	static Main main;
	
	PluginManager pm = Bukkit.getServer().getPluginManager();
	
	//Stores current selections block of an admin. Used via command
	public static HashMap<String, String> selections = new HashMap<String, String>();
	
	//Material used to select a reward
	
	public static int selectionItem = Material.EMERALD.getId();
	
	//Stores all known reward locations
	public static HashMap<String, QBlock> qBlocks = new HashMap<String, QBlock>();
	
	public static final String prefix = "§7[§aQuestionBlocks§7] §a";
	public static final String errPrefix = "§7[§cQuestionBlocks§7] §c";
	
	public static final String help = ChatColor.GREEN + "/qblock" + ChatColor.AQUA + " - Root command. Aliases: qblocks, qb, questionblocks.\n"
				+ ChatColor.GREEN + "/qblock help" + ChatColor.AQUA + " - displays help.\n"
				+ ChatColor.GREEN + "/qblock create <name> <item> <type> <time>" + ChatColor.AQUA + " - create a question block with the selected location.\n"
				+ ChatColor.GREEN + "/qblock delete" + ChatColor.AQUA + " - delete the selected question block.\n"
				+ ChatColor.GREEN + "/qblock info" + ChatColor.AQUA + " - gives you info about the current selected qblock.\n"
				+ ChatColor.GREEN + "/qblock list" + ChatColor.AQUA + " - lists all qblocks.\n"
				+ ChatColor.GREEN + "/qblock set time <time>" + ChatColor.AQUA + " - set the delay of the current qblock.\n"
				+ ChatColor.GREEN + "/qblock set item <item>" + ChatColor.AQUA + " - set the item of the current qblock.\n"
				+ ChatColor.GREEN + "/qblock set type <inv/pop>" + ChatColor.AQUA + " - set the how the player should receive the item.\n"
				+ ChatColor.GREEN + "/qblock updateskin" + ChatColor.AQUA + " - Updates the selected block's skin to the one in the config.";

	public void onEnable(){
		main = this;
		
		//Add defaults
		getConfig().addDefault("selectionItem", Material.EMERALD.getId());
		
		//Default value is the lucky block head.
		getConfig().addDefault("skinURL", "http://textures.minecraft.net/texture/b3b710b08b523bba7efba07c629ba0895ad61126d26c86beb3845603a97426c");
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		selectionItem = getConfig().getInt("selectionItem");
		
		try {
			if(getConfig().getConfigurationSection("qblocks") != null){
				for(String loc : getConfig().getConfigurationSection("qblocks.").getKeys(false)){
					String key = "qblocks." + loc;
					String name = getConfig().getString(key + ".name");
					int delay = getConfig().getInt(key + ".delay");
					@SuppressWarnings("unchecked")
					ArrayList<ItemStack> items = (ArrayList<ItemStack>) getConfig().getList(key + ".items");
					List<String> receivedRaw = getConfig().getStringList(key + ".received");
					List<String> delaysRaw = getConfig().getStringList(key + ".delays");
					ConcurrentHashMap<String, Integer> received = new ConcurrentHashMap<String, Integer>();
					ConcurrentHashMap<String, Long> delays = new ConcurrentHashMap<String, Long>();
					for(String r : receivedRaw){
						String[] parts = r.split(":");
						received.put(parts[0], Integer.parseInt(parts[1]));
					}
					for(String r : delaysRaw){
						String[] parts = r.split(":");
						delays.put(parts[0], Long.parseLong(parts[1]));
					}
					String type = getConfig().getString(key + ".type", "inv");
					qBlocks.put(loc, new QBlock(name, delay, items, loc, received, delays, type));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pm.registerEvents(new BlockClickEvent(), this);
		
		getCommand("questionblock").setExecutor(new QBlockCommand());
	}
	
	public void onDisable(){
		getConfig().set("qblocks", null);
		for(Entry<String, QBlock> e : qBlocks.entrySet()){
			try {
				String key = "qblocks." + e.getKey();
				QBlock r = e.getValue();
				getConfig().set(key + ".name", r.getName());
				getConfig().set(key + ".delay", r.getDelay());
				getConfig().set(key + ".items", r.getRewardItems());
				ArrayList<String> received = new ArrayList<String>();
				for(String uuid : r.getReceived().keySet()) received.add(uuid + ":" + r.getReceived().get(uuid).intValue());
				getConfig().set(key + ".received", received);
				ArrayList<String> delays = new ArrayList<String>();
				for(String uuid : r.getDelays().keySet()) delays.add(uuid + ":" + r.getDelays().get(uuid).longValue());
				getConfig().set(key + ".delays", delays);
				getConfig().set(key + ".type", r.getType());
			} catch (Exception e2) {e2.printStackTrace();}
		}
		saveConfig();
	}
	
	public static Main getInstance(){
		return main;
	}
	
}

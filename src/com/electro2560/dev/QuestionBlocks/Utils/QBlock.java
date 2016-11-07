package com.electro2560.dev.QuestionBlocks.Utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class QBlock{
	
	String name = null;
	int delay = 0;
	ArrayList<ItemStack> rewardItems = new ArrayList<ItemStack>();
	String loc = null;
	//Array list of any players that receive the one time reward.
	ConcurrentHashMap<String, Integer> received = new ConcurrentHashMap<String, Integer>();
	//Stores all delays
	ConcurrentHashMap<String, Long> delays = new ConcurrentHashMap<String, Long>();
	
	String type = "inv";
	
	//Create an instance that holds each reward
	public QBlock(String name, int delay, ArrayList<ItemStack> rewardItems, String loc, ConcurrentHashMap<String, Integer> received, ConcurrentHashMap<String, Long> delays, String type){
		this.name = name;
		this.delay = delay;
		this.rewardItems = rewardItems;
		this.loc = loc;
		if(received != null){
			this.received = received;
		}
		if(delays != null){
			this.delays = delays;
		}
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		try {
			this.name = ChatColor.translateAlternateColorCodes('&', name).trim();
		} catch (Exception e) {}
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public ArrayList<ItemStack> getRewardItems() {
		return rewardItems;
	}
	
	public void setRewardItems(ArrayList<ItemStack> rewardItems) {
		this.rewardItems = rewardItems;
	}
	
	public String getLocation() {
		return loc;
	}
	
	public ConcurrentHashMap<String, Integer> getReceived() {
		return received;
	}
	
	public ConcurrentHashMap<String, Long> getDelays() {
		return delays;
	}
	
	public void setReceived(ConcurrentHashMap<String, Integer> received) {
		this.received = received;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDelays(ConcurrentHashMap<String, Long> delays) {
		this.delays = delays;
	}

}

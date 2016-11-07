package com.electro2560.dev.QuestionBlocks.Utils;

import java.util.Base64;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.electro2560.dev.QuestionBlocks.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.TileEntitySkull;

public class Utils {
	
	public static boolean checkDelay(Player p, QBlock r){
		
		String key = p.getUniqueId().toString();
		
		Long millsecBefore = (Long) r.getDelays().get(p.getUniqueId().toString());
		Long wait = Long.valueOf(System.currentTimeMillis());
		
		//Check if exists, then execute
		if(millsecBefore != null){
			
			//Current wait time
			Long msWait = Long.valueOf(wait.longValue() - millsecBefore.longValue());
			Long msWaitFormated = Long.valueOf(1000 * r.getDelay());
			
			//Check if it can be used yet
			if(msWait < msWaitFormated){
				//Cannot be used yet
				p.sendMessage(Main.errPrefix + ChatColor.RED + "Too soon, you must wait: " + ChatColor.AQUA + timeToMinSec(msWait - msWaitFormated));
				return false;
			}
		}
		//Can be used. Reset the delay.
		r.getDelays().put(key, wait);
		return true;
	}
	
	//Method to format into minutes and seconds
	public static String timeToMinSec(Long ms){
		int seconds = (int)(ms / 1000 % 60);
		int minutes = (int)(ms / 1000 / 60 % 60);
		int hours = (int)(ms / 1000 / 60 / 60 % 24);
		int days = (int)(ms / 1000 / 60 /60 / 24);
		
		return String.format("%1dd %02dh %02dm %02ds", new Object[] {Math.abs(days), Math.abs(hours), Math.abs(minutes), Math.abs(seconds)});
	}
	
	public static String locToString(Location l){
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
	}
	
	public static Location stringToLoc(String s){
		String[] parts = s.split(":");
		return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
	}
	
	private static GameProfile getNonPlayerProfile(String skinData, boolean randomName) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", skinData).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
       
        return profile;
	}

	@SuppressWarnings("deprecation")
	public static void setSkullWithNonPlayerProfile(String skinData, boolean randomName, Block skull) {
		if(skull.getType() != Material.SKULL) throw new IllegalArgumentException("Block must be a skull.");
		TileEntitySkull skullTile = (TileEntitySkull)((CraftWorld)skull.getWorld()).getHandle().getTileEntity(new BlockPosition(skull.getX(), skull.getY(), skull.getZ()));
	
		GameProfile gp = getNonPlayerProfile(skinData, randomName);
		skullTile.setGameProfile(gp);
	
		skull.getWorld().refreshChunk(skull.getChunk().getX(), skull.getChunk().getZ());
	}
	
}

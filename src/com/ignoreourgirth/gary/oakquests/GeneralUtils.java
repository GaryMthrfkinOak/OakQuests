/*******************************************************************************
 * Copyright (c) 2012 GaryMthrfkinOak (Jesse Caple).
 * 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.ignoreourgirth.gary.oakquests;

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GeneralUtils {

	public static boolean chance(double dropRate) {
		Random random = new Random();
		return (dropRate >= random.nextDouble());
	}
	
	public static void sendFromNPC(Player player, String NPCName, String message) { 
		player.sendMessage("§a[NPC] §f" + NPCName + "§a: " + message); 
	}
	
	public static void setWaypoint(Player player, Location location) {
		player.setCompassTarget(location);
		player.sendMessage(ChatColor.GRAY + "Waypoint Set (" + 
				location.getWorld().getName() + 
				", X: " + ((int) Math.round(location.getX())) +  
				", Y: " + ((int) Math.round(location.getY())) + 
				", Z: " + ((int) Math.round(location.getZ())) + ")");
	}
	
	public static void clearWaypoint(Player player) {
		player.setCompassTarget(new Location(OakQuests.server.getWorld("new_world"), 0,0,0));
	}

}

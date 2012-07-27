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
package com.ignoreourgirth.gary.oakquests.quests;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.KeyItemEntity;
import com.ignoreourgirth.gary.oakquests.MagicUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;
import com.ignoreourgirth.gary.oakquests.events.KeyItemAddedEvent;

public class TheHermetPart3 extends Quest {

	private final int markID = 3;
	private final String orbItemName = "Power Orb";
	private final Location waypointLocation = new Location(OakQuests.server.getWorld("new_world"), 1265.8431, 65, 3449.0183);
	private final Location orbLocation = new Location(OakQuests.server.getWorld("new_world"), 1317.4335, 28, 3447.4670);
	private final Location hermetLocation = new Location(OakQuests.server.getWorld("new_world"), 1548.2684, 67, 3204.4282, -88.2f, 0f);
	
	private final Location rsTorchLocation01 = new Location(OakQuests.server.getWorld("new_world"), 1279, 29, 3452);
	private final Location rsTorchLocation02 = new Location(OakQuests.server.getWorld("new_world"), 1279, 29, 3453);
	private final Location rsTorchLocation03 = new Location(OakQuests.server.getWorld("new_world"), 1299, 29, 3455);
	private final Location rsTorchLocation04 = new Location(OakQuests.server.getWorld("new_world"), 1299, 29, 3456);
	private final Location rsTorchLocation05 = new Location(OakQuests.server.getWorld("new_world"), 1303, 32, 3443);
	private final Location rsTorchLocation06 = new Location(OakQuests.server.getWorld("new_world"), 1306, 28, 3428);
	
	@Override
	public void initialize() {
		questID = "Hermet3";
		questTitle = "The Hermet, Part III";
		questNPC = "Theo";
		isRepeatable = false;
		unavailibleMessage = null;
		notStartedTurnInMessage = null;
	}

	@Override
	protected void postInitialize() {
		for (Player player : OakQuests.server.getOnlinePlayers()) {
			if (isOnQuest(player)) {
				if ((OakQuests.keyItems.getItemCount(player, orbItemName) == 0)) {
					new KeyItemEntity(this, player, orbLocation, orbItemName, Material.MAGMA_CREAM);
				}
			}
		}
		OakQuests.server.getScheduler().scheduleSyncRepeatingTask(OakQuests.plugin, new Runnable() {
    		public void run() {
    			if (rsTorchLocation01.getBlock().getType() == Material.AIR) {
    				rsTorchLocation01.getBlock().setType(Material.REDSTONE_TORCH_ON);
    				rsTorchLocation02.getBlock().setType(Material.REDSTONE_TORCH_ON);
    				rsTorchLocation03.getBlock().setType(Material.REDSTONE_TORCH_ON);
    				rsTorchLocation04.getBlock().setType(Material.REDSTONE_TORCH_ON);
    				rsTorchLocation05.getBlock().setType(Material.REDSTONE_TORCH_ON);
    				rsTorchLocation06.getBlock().setType(Material.REDSTONE_TORCH_ON);
    			} else {
    				rsTorchLocation01.getBlock().setType(Material.AIR);
    				rsTorchLocation02.getBlock().setType(Material.AIR);
    				rsTorchLocation03.getBlock().setType(Material.AIR);
    				rsTorchLocation04.getBlock().setType(Material.AIR);
    				rsTorchLocation05.getBlock().setType(Material.AIR);
    				rsTorchLocation06.getBlock().setType(Material.AIR);
    			}
    		}
    	}, 0, 50);
	}
	
	@Override
	protected void pluginUnload() {
		rsTorchLocation01.getBlock().setType(Material.REDSTONE_TORCH_ON);
		rsTorchLocation02.getBlock().setType(Material.REDSTONE_TORCH_ON);
		rsTorchLocation03.getBlock().setType(Material.REDSTONE_TORCH_ON);
		rsTorchLocation04.getBlock().setType(Material.REDSTONE_TORCH_ON);
		rsTorchLocation05.getBlock().setType(Material.REDSTONE_TORCH_ON);
		rsTorchLocation06.getBlock().setType(Material.REDSTONE_TORCH_ON);
	}
	
	@Override
	protected boolean isAvailable(Player player) {
		return (OakQuests.loader.getCompletionCount("Hermet2", player) > 0);
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Ein ohb rahmanz.");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Goowd.");
		GeneralUtils.setWaypoint(player, waypointLocation);
		MagicUtils.setRecallLocation(player, hermetLocation);
		new KeyItemEntity(this, player, orbLocation, orbItemName, Material.MAGMA_CREAM);
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Vhil you joost. go. gut. svee. ohb!?");
	}

	@Override
	protected void dropped(Player player) {
		 OakQuests.keyItems.removeItemType(player, orbItemName);
	}

	@Override
	protected void information(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
			player.sendMessage(ChatColor.WHITE + "You have the final orb. Use the " + ChatColor.GOLD  + "recall " + ChatColor.WHITE + "spell to return to " + ChatColor.GOLD  + questNPC + ChatColor.WHITE + ".");
		} else {
			player.sendMessage(ChatColor.GOLD  + questNPC + ChatColor.WHITE + " wants one last orb.");
			GeneralUtils.setWaypoint(player, waypointLocation);
		}
	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
			GeneralUtils.sendFromNPC(player, questNPC,  "This shall be poorfukt! Dankyou.");
			OakQuests.keyItems.removeItemType(player, orbItemName);
			CommonRewards.giveMoney(player, 40000);
			CommonRewards.increaseMaxMP(player, 50);
			GeneralUtils.sendFromNPC(player, questNPC,  "Take mein spell. May it serve you.");
			MagicUtils.teachSpell(questNPC, player, markID, 1);
			MagicUtils.setRecallLocation(player, null);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Go get svhee ohb.");
			return false;
		}
	}
	
	@Override
	protected Location waypoint(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) == 0) {
			return waypointLocation;
		} else {
			return hermetLocation;
		}
	}
	
	@EventHandler
	public void onKeyItemAdded(KeyItemAddedEvent event) {
		Player player = event.getPlayer();
		if (isOnQuest(player)) {
			if (event.getItemName().equals(orbItemName)) {
				MagicUtils.setRecallLocation(player, hermetLocation);
				player.sendMessage(ChatColor.WHITE + "You have the final orb. Use the " + ChatColor.GOLD  + "recall " + ChatColor.WHITE + "spell to return to " + ChatColor.GOLD  + questNPC + ChatColor.WHITE + ".");
				MagicUtils.setRecallLocation(player, hermetLocation);
			}
		}
	}
	
	

	
}

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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class ScandiumRepeatable extends Quest {

	private final int oreNeeded = 25;
	private final double oreDropRate = 0.02D;
	private final String keyItemName = "Thortveitite Ore";
	
	@Override
	public void initialize() {
		questID = "Mine1";
		questTitle = "Mining Thortveitite";
		questNPC = "Engineer";
	}
	
	@Override
	protected void postInitialize() {	
	}
	
	@Override
	protected void pluginUnload() {
	}

	@Override
	protected boolean isAvailable(Player player) {
		return true;
	}

	@Override
	protected void ask(Player player) {
		int completionCount = (OakQuests.loader.getCompletionCount(questID, player));
		if  (completionCount == 0){
			GeneralUtils.sendFromNPC(player, questNPC,  "Looking for mining a job? I pay well.");
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Want to find me more thortveitite?");
		}
	}

	@Override
	protected void accepted(Player player) {
		int completionCount = (OakQuests.loader.getCompletionCount(questID, player));
		if  (completionCount == 0){
			GeneralUtils.sendFromNPC(player, questNPC,  "Great. We need scandium for our project. Mine out some thortveitite and return here.");
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "You know the drill. Mine some thortveitite and return here.");
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (isOnQuest(player)) {
			Block block = event.getBlock();
			if (block.getType() == Material.STONE) {
				if (GeneralUtils.chance(oreDropRate)) {
					int oreCount = OakQuests.keyItems.getItemCount(player, keyItemName);
					if (oreCount < oreNeeded) {
						OakQuests.keyItems.addItem(player, keyItemName);
						player.sendMessage(ChatColor.GRAY + "Found " + keyItemName + ".  " + (oreCount + 1) + "/" + oreNeeded);
					}
					if (oreCount + 1 == oreNeeded) {
						player.sendMessage(ChatColor.GRAY + "You have found enough ore to complete the quest.");
					}
				}
			}
		}
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Fair enough. Labor is cheap.");
	}

	@Override
	protected void dropped(Player player) {
		OakQuests.keyItems.removeItemType(player, keyItemName);
	}

	@Override
	protected void information(Player player) {
		int oreCount = OakQuests.keyItems.getItemCount(player, keyItemName);
		if (oreCount >= oreNeeded) {
			player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the " + ChatColor.GOLD  + questNPC + ".");
		} else {
			player.sendMessage(ChatColor.WHITE + "Mine out " + ChatColor.GOLD  + (oreNeeded - oreCount) + ChatColor.WHITE + " more shards of " + ChatColor.GOLD + keyItemName + ChatColor.WHITE  + ".");
		}

	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, keyItemName) >= oreNeeded) {
			GeneralUtils.sendFromNPC(player, questNPC,  "That's the stuff. Here's your compensation.");
			OakQuests.keyItems.removeItemType(player, keyItemName);
			CommonRewards.giveMoney(player, 10000);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Come back when the job is actually finished.");
			return false;
		}
	}
	
	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

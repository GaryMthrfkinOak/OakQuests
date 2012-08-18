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

import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class Exploration1 extends Quest {

	private int itemsNeeded = 500;
	private String keyItemNameGiven = "Enchanted Mapping Device";
	private String keyItemNameNeeded = "Areas Explored";
	
	@Override
	public void initialize() {
		questID = "Explorer";
		questTitle = "Charting the World";
		questNPC = "Cartographer";
		isRepeatable = true;
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
			GeneralUtils.sendFromNPC(player, questNPC,  "Yes, I do have a job for you if you can explore uncharted regions for me. Does that sound interesting to you?");
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Yes, I'm still looking for explorers.");
		}
	}

	@Override
	protected void accepted(Player player) {
		int completionCount = (OakQuests.loader.getCompletionCount(questID, player));
		if  (completionCount == 0){
			GeneralUtils.sendFromNPC(player, questNPC,  "I'm glad to be working with you. " +
					"Take this device. As you travel to previously unexplored areas, " +
					"this device will make use of a few subconscious regions of your mind. " +
					"It will put to memory a rough draft of the terrain which I can then recall later when you finish the job. " +
					"No, it's perfectly safe, I promise... Did I mention that I pay well?");
			OakQuests.keyItems.addItem(player, keyItemNameGiven);
			player.sendMessage(ChatColor.GRAY + "You obtained the " + ChatColor.ITALIC + keyItemNameGiven + ChatColor.RESET + ChatColor.GRAY + ".");
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "You should know the drill by now. Go explore.");
			OakQuests.keyItems.addItem(player, keyItemNameGiven);
			player.sendMessage(ChatColor.GRAY + "You obtained the " + ChatColor.ITALIC + keyItemNameGiven + ChatColor.RESET + ChatColor.GRAY + ".");
		}
	}
	
//	@EventHandler (priority=EventPriority.MONITOR)
//	public void onChunkLoad(ChunkLoadEvent event) {
//		if (event.isNewChunk()) {
//			newChunks.add(event.getChunk());
//		}
//	}
//	
//	@EventHandler (priority=EventPriority.MONITOR)
//	public void onChunkPopulate(ChunkPopulateEvent event) {
//		Chunk chunk = event.getChunk();
//		if (newChunks.contains(chunk)) {
//			for (Entity entity : chunk.getEntities()) {
//				if (entity instanceof Player) {
//					Player player = (Player) entity;
//					if (isOnQuest(player)) {
//						OakQuests.keyItems.addItem(player, keyItemNameNeeded);
//					}
//				}
//			}
//			newChunks.remove(chunk);
//		}
//	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "If you change your mind I'll be here.");
	}

	@Override
	protected void dropped(Player player) {
		 OakQuests.keyItems.removeItemType(player, keyItemNameGiven);
		 OakQuests.keyItems.removeItemType(player, keyItemNameNeeded);
	}

	@Override
	protected void information(Player player) {
		int itemCount = OakQuests.keyItems.getItemCount(player, keyItemNameNeeded);
		if (itemCount >= itemsNeeded) {
			player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the " + ChatColor.GOLD  + questNPC);
		} else {
			player.sendMessage(ChatColor.WHITE + "Explore uncharted regions. " + ChatColor.GOLD + itemCount + ChatColor.WHITE + "/" + ChatColor.GOLD  + (itemsNeeded) + ChatColor.WHITE + " areas discovered.");
		}

	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, keyItemNameNeeded) >= itemsNeeded) {
			GeneralUtils.sendFromNPC(player, questNPC,  "You’re done already? Well then, let’s just extract that data. Hold still for a second. *He grasps your face with the palm of his hand* Let’s see... *grips tight, you squirm* Come on...  *grasps harder* There we go... and we’re done. Thank you for your work. Here is your compensation.");
			OakQuests.keyItems.removeItemType(player, keyItemNameGiven);
			OakQuests.keyItems.removeItemType(player, keyItemNameNeeded);
			CommonRewards.giveMoney(player, 20000);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "I can see just by looking at you that you haven't finished the job yet.");
			return false;
		}
	}

	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

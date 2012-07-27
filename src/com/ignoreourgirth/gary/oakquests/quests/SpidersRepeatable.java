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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class SpidersRepeatable extends Quest {

	private final int silkNeeded = 32;
	private final double silkDropRate = 0.1D;
	private final String keyItemName = "Spider Silk";
	
	@Override
	public void initialize() {
		questID = "Mobs1";
		questTitle = "Questionable Fashion";
		questNPC = "Fashionista";
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
			GeneralUtils.sendFromNPC(player, questNPC,  "I hava a job for you. It involves spiders.");
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Yes, I could always use more.");
		}
	}

	@Override
	protected void accepted(Player player) {
		int completionCount = (OakQuests.loader.getCompletionCount(questID, player));
		if  (completionCount == 0){
			GeneralUtils.sendFromNPC(player, questNPC,  "I need spider silk... Don't ask why!");
			GeneralUtils.sendFromNPC(player, questNPC,  "...Just go get it!");
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Good. Go get me some spider silk.");
		}
		
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Spider) {
			Player player  = ((Spider) entity).getKiller();
			if (player != null) {
				if (isOnQuest(player)) {
					if (GeneralUtils.chance(silkDropRate)) {
						int silkCount = OakQuests.keyItems.getItemCount(player, keyItemName);
						if (silkCount < silkNeeded) {
							OakQuests.keyItems.addItem(player, keyItemName);
							player.sendMessage(ChatColor.GRAY + "Found " + keyItemName + ".  " + (silkCount + 1) + "/" + silkNeeded);
						}
						if (silkCount + 1 == silkNeeded) {
							player.sendMessage(ChatColor.GRAY + "You have found enough " + keyItemName + " to complete the quest.");
						}
					}
				}
			}
		}
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Bugger off then!");
	}

	@Override
	protected void dropped(Player player) {
		OakQuests.keyItems.removeItemType(player, keyItemName);
	}

	@Override
	protected void information(Player player) {
		int oreCount = OakQuests.keyItems.getItemCount(player, keyItemName);
		if (oreCount >= silkNeeded) {
			player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the " + ChatColor.GOLD  + questNPC + ".");
		} else {
			player.sendMessage(ChatColor.WHITE + "Kill " + ChatColor.GOLD  + "Spiders " + ChatColor.WHITE + "for silk. " + ChatColor.GOLD + (silkNeeded - oreCount) + ChatColor.WHITE + " more reams of " + ChatColor.GOLD + keyItemName + ChatColor.WHITE  + " remaining.");
		}

	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, keyItemName) >= silkNeeded) {
			GeneralUtils.sendFromNPC(player, questNPC,  "Ooh, this will look marvelous. Here you go.");
			OakQuests.keyItems.removeItemType(player, keyItemName);
			CommonRewards.giveMoney(player, 10000);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "What? Are you stupid or something? Where's my silk you prat?.");
			return false;
		}
	}
	
	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

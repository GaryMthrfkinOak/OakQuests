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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.ignoreourgirth.gary.oakcorelib.MagicUseEvent;
import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.MagicUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class HolySpellQuest extends Quest {

	private final int holySpellID = 23;
	private final int ashNeeded = 5;
	private final double ashDropRate = 0.2D;
	private final String keyItemName = "Unholy Ash";
	
	@Override
	public void initialize() {
		questID = "Holy";
		questTitle = "White Magic Experiment";
		questNPC = "Student";
		isRepeatable = false;
	}
	
	@Override
	protected void postInitialize() {	
	}
	
	@Override
	protected void pluginUnload() {
	}

	@Override
	protected boolean isAvailable(Player player) {
		return (MagicUtils.spellsKnown(player) > 4);
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "You may be able to help. I need someone to test a magic theory for me.");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Excellent! I need you to cast this spell on as many zombies and skelletons as you can. Make sure to collect the ash. About " + ashNeeded + " piles of ash should do.");
		MagicUtils.teachSpell(questNPC, player, holySpellID, 1);
	}
	
	@EventHandler
	public void onMagicUse(MagicUseEvent event) {
		Player player = event.getCaster();
		if (isOnQuest(player)) {
			if (event.getSpellID() == holySpellID) {
				if (event.getEntity() != null) {
					if (event.getEntity().getType() == EntityType.ZOMBIE || event.getEntity().getType() == EntityType.SKELETON) {
						if (GeneralUtils.chance(ashDropRate)) {
							int ashCount = OakQuests.keyItems.getItemCount(player, keyItemName);
							if (ashCount < ashNeeded) {
								OakQuests.keyItems.addItem(player, keyItemName);
								player.sendMessage(ChatColor.GRAY + "Found a pile of " + keyItemName + ".  " + (ashCount + 1) + "/" + ashNeeded);
							}
							if (ashCount + 1 == ashNeeded) {
								player.sendMessage(ChatColor.GRAY + "That should be enough ash. I should return to the Student.");
							}
						}

					}
				}
			}
		}
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Fine. Let me know if you change your mind.");
	}

	@Override
	protected void dropped(Player player) {
		 OakQuests.keyItems.removeItemType(player, keyItemName);
	}

	@Override
	protected void information(Player player) {
		int ashCount = OakQuests.keyItems.getItemCount(player, keyItemName);
		if (ashCount >= ashNeeded) {
			player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the " + ChatColor.GOLD  + "Student.");
		} else {
			player.sendMessage(ChatColor.WHITE + "Kill " + ChatColor.GOLD  + (ashNeeded - ashCount) + ChatColor.WHITE + " more " + ChatColor.GOLD + "zombies or skeletons" + ChatColor.WHITE  + ".");
		}

	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, keyItemName) >= ashNeeded) {
			GeneralUtils.sendFromNPC(player, questNPC,  "Wonderful! Look at all that zombified ash. Lovely.");
			OakQuests.keyItems.removeItemType(player, keyItemName);
			CommonRewards.increaseMaxMP(player, 30);
			CommonRewards.giveXP(player, 300);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "The data is still inconclusive. Collect more ash.");
			return false;
		}
	}

	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

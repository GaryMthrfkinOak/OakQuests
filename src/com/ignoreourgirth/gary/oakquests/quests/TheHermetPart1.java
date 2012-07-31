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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.MagicUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;
import com.ignoreourgirth.gary.oakquests.events.KeyItemAddedEvent;

public class TheHermetPart1 extends Quest {

	private final int recallID = 4;
	private final String orbItemName = "Power Orb";
	private final Location orbLocation = new Location(OakQuests.server.getWorld("new_world"), -3635.4143, 65, 9819.4858);
	private final Location hermetLocation = new Location(OakQuests.server.getWorld("new_world"), 1548.2684, 67, 3204.4282, -88.2f, 0f);
	
	
	@Override
	public void initialize() {
		questID = "Hermet1";
		questTitle = "The Hermet, Part I";
		questNPC = "Theo";
		isRepeatable = false;
		unavailibleMessage = null;
		notRepeatbleMessage = null;
		notStartedTurnInMessage = null;
	}

	@Override
	protected void postInitialize() {
		for (OfflinePlayer player : OakQuests.server.getOfflinePlayers()) {
			if (isOnQuest(player)) {
				if ((OakQuests.keyItems.getItemCount(player, orbItemName) == 0)) {
					OakQuests.keyItems.spawnDisplayItem(orbLocation, orbItemName, Material.MAGMA_CREAM, player);
				}
			}
		}
	}
	
	@Override
	protected void pluginUnload() {
	}
	
	@Override
	protected boolean isAvailable(Player player) {
		if (MagicUtils.spellsKnown(player) > 2) {
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC, "Mien sviet mutter can muhgick butter zven you.");
		}
		return false;
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Svee ohbz. Go get me svem. Ja?");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Dankyou, go gut me zvat I need.");
		GeneralUtils.setWaypoint(player, orbLocation);
		if (!MagicUtils.knowsSpell(player, recallID, 1)) {
			GeneralUtils.sendFromNPC(player, questNPC,  "Und use svis to come back offtah.");
			MagicUtils.teachSpell(questNPC, player, recallID, 1);
		}
		MagicUtils.setRecallLocation(player, hermetLocation);
		OakQuests.keyItems.spawnDisplayItem(orbLocation, orbItemName, Material.MAGMA_CREAM, player);
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Zvat? Go avay you jokeh.");
	}

	@Override
	protected void dropped(Player player) {
		 OakQuests.keyItems.removeItemType(player, orbItemName);
	}

	@Override
	protected void information(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
			player.sendMessage(ChatColor.WHITE + "You have the orb. Use the " + ChatColor.GOLD  + "recall " + ChatColor.WHITE + "spell to return to " + ChatColor.GOLD  + questNPC + ChatColor.WHITE + ".");
		} else {
			player.sendMessage(ChatColor.WHITE + "A hermet named " +  ChatColor.GOLD  + questNPC + ChatColor.WHITE + " wanted an orb at the given waypoint.");
			GeneralUtils.setWaypoint(player, orbLocation);
		}
	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
			GeneralUtils.sendFromNPC(player, questNPC,  "Vunderbar! Two moore remain.");
			OakQuests.keyItems.removeItemType(player, orbItemName);
			CommonRewards.giveXP(player, 200);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Sver ist zvee ohb?");
			return false;
		}
	}
	
	@Override
	protected Location waypoint(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) == 0) {
			return orbLocation;
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
				player.sendMessage(ChatColor.WHITE + "You have the orb. Use the " + ChatColor.GOLD  + "recall " + ChatColor.WHITE + "spell to return to " + ChatColor.GOLD  + questNPC + ChatColor.WHITE + ".");
				MagicUtils.setRecallLocation(player, hermetLocation);
			}
		}
	}
	
	

	
}

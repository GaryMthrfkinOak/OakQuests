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
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class LavaLava extends Quest {

	@Override
	public void initialize() {
		questID = "LavaLava";
		questTitle = "The Floor is Lava";
		questNPC = "Blacksmith";
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
		return true;
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC, "Well, we do need more fuel for the furnace. We've been running low. If you get me a couple buckets of lava, I'll make it worth your while.");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Thanks. See you again soon.");
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Don't think there's anything else that needs doing. Sorry.");
	}

	@Override
	protected void dropped(Player player) {
	}

	@Override
	protected void information(Player player) {
		player.sendMessage(ChatColor.WHITE + "The " + ChatColor.GOLD  + "Blacksmith " + ChatColor.WHITE + "asked you to get " + ChatColor.GOLD + "2 buckets of lava" + ChatColor.WHITE  + ".");
	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (player.getInventory().contains(Material.LAVA_BUCKET, 2)) {
			player.getInventory().setItem(player.getInventory().first(Material.LAVA_BUCKET), new ItemStack(Material.BUCKET));
			player.getInventory().setItem(player.getInventory().first(Material.LAVA_BUCKET), new ItemStack(Material.BUCKET));
			GeneralUtils.sendFromNPC(player, questNPC,  "Thanks. We'll put it to use.");
			CommonRewards.giveMoney(player, 5000);
			CommonRewards.giveXP(player, 150);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Lava. Buckets of Lava. Two of them.");
			return false;
		}
	}
	
	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;

public class CommonRewards {

	public static void giveMoney(Player player, int amount) {
		OakCoreLib.getEconomy().bankDeposit(player.getName(), amount);
		if (amount == 1) {
			player.sendMessage(ChatColor.DARK_AQUA + "Gained " + StringFormats.toCurrency(amount) + ".");
		} else {
			player.sendMessage(ChatColor.DARK_AQUA + "Gained " + StringFormats.toCurrency(amount) + ".");
		}
	}
	
	public static void giveXP(Player player, int amount) {
		OakCoreLib.getXPMP().addXP(player, amount);
		player.sendMessage(ChatColor.DARK_AQUA + "Gained " + amount + " xp.");
	}
	
	public static void increaseMaxMP(Player player, int amount) {
		OakCoreLib.getXPMP().setMaxMP(player, OakCoreLib.getXPMP().getMaxMP(player) + amount);
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Your maximum MP has increased by " + amount + ".");
	}
	
	public static void giveItem(Player player, ItemStack item) {
		String nullString = null;
		giveItem(player, item, nullString);
	}
	
	public static void giveItem(Player player, ItemStack itemStack, String specialName) {
		PlayerInventory inventory = player.getInventory();
		inventory.setItem(inventory.firstEmpty(), itemStack);
		if (specialName != null) {
			player.sendMessage(ChatColor.DARK_AQUA + "Item given: " + specialName);
		} else {
			if (itemStack.getAmount() == 1) {
				player.sendMessage(ChatColor.DARK_AQUA + "Item given: " + itemStack.getType().toString());
			} else {
				player.sendMessage(ChatColor.DARK_AQUA + "Items given: " + itemStack.getAmount() + " " + itemStack.getType().toString());
			}
		}
	}
	
	public static boolean enoughSpaceInInventory(Player player, int itemCount) {
		int emptySlots = 0;
		ItemStack[] inventoryItems = player.getInventory().getContents();
		for (int i = 0; i < inventoryItems.length; i++) {
		    if (inventoryItems[i] == null) emptySlots++;
		}
		return (emptySlots >= itemCount);
	}
}

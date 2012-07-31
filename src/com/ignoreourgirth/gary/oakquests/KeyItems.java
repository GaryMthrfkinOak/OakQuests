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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.ignoreourgirth.gary.oakcorelib.DisplayItems;
import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.ProximityDetection;
import com.ignoreourgirth.gary.oakcorelib.ProximityEvent;
import com.ignoreourgirth.gary.oakquests.events.KeyItemAddedEvent;

public class KeyItems implements Listener {

	protected static HashMap<String, HashMap<String, Integer>> itemTables;
	public static HashMap<Integer,Integer> displayItemIDs;
	public static HashMap<Integer,String> displayItemPlayers;
	public static HashMap<Integer,String> displayItemNames;
	
	public KeyItems() {
		itemTables = new HashMap<String, HashMap<String, Integer>>();
		displayItemIDs = new HashMap<Integer,Integer>();
		displayItemPlayers = new HashMap<Integer,String>();
		displayItemNames = new HashMap<Integer,String>();
		for (OfflinePlayer player : OakQuests.server.getOfflinePlayers()) {
			itemTables.put(player.getName(), new HashMap<String, Integer>());
		}
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT player, item, count FROM oakquests_keyitems;");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				OfflinePlayer nextPlayer = OakQuests.server.getOfflinePlayer(result.getString(1));
				itemTables.get(nextPlayer.getName()).put(result.getString(2), result.getInt(3));
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}

	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!itemTables.containsKey(event.getPlayer().getName())) {
			itemTables.put(event.getPlayer().getName(), new HashMap<String, Integer>());
		}
	}
	
	@EventHandler
	public void onProximity(ProximityEvent event) {
		int regionID = event.getRegionID();
		if (displayItemNames.containsKey(regionID)) {
			
			Player eventPlayer = event.getPlayer();
			String targetPlayerName = displayItemPlayers.get(regionID);
			if (targetPlayerName == null) return;
			if (!targetPlayerName.equals(eventPlayer.getName())) return;
			
			String name = displayItemNames.get(regionID);
			int itemID = displayItemIDs.get(regionID);
			
			displayItemIDs.remove(regionID);
			displayItemNames.remove(regionID);
			displayItemPlayers.remove(regionID);
			
			DisplayItems.removeItem(itemID);
			ProximityDetection.remove(regionID);
			
			event.getPlayer().sendMessage(ChatColor.GRAY + "Picked up " + ChatColor.GOLD + name + ChatColor.GRAY + ".");
			OakQuests.keyItems.addItem(event.getPlayer(), name);
		}
	}
	
	public void addItem(Player player, String itemName) {
		HashMap<String, Integer> playerItems = itemTables.get(player.getName());
		int itemCount = 1;
		if (playerItems.containsKey(itemName)) {
			itemCount = playerItems.get(itemName) + 1;
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("UPDATE oakquests_keyitems SET count=? WHERE player=? AND item=?");
				statement.setInt(1, itemCount);
				statement.setString(2, player.getName());
				statement.setString(3, itemName);
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				OakQuests.log.log(Level.SEVERE, e.getMessage());
			}
		} else {
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakquests_keyitems (player, item, count) VALUES (?, ?, ?)");
				statement.setString(1, player.getName());
				statement.setString(2, itemName);
				statement.setInt(3, 1);
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				OakQuests.log.log(Level.SEVERE, e.getMessage());
			}
		}
		playerItems.put(itemName, itemCount);
		OakQuests.server.getPluginManager().callEvent(new KeyItemAddedEvent(player, itemName, itemCount));
	}
	
	public void spawnDisplayItem(Location location, String keyItemName, Material material) {
		spawnDisplayItem(location, keyItemName, material, null);
	}
	
	public void spawnDisplayItem(Location location, String keyItemName, Material material, OfflinePlayer player) {
		int displayItemID = DisplayItems.newItem(new ItemStack(material), location, OakQuests.plugin);
		int proximityID = ProximityDetection.add(OakQuests.plugin, location, 2);
		if (player != null) displayItemPlayers.put(proximityID, player.getName());
		displayItemIDs.put(proximityID, displayItemID);
		displayItemNames.put(proximityID, keyItemName);
	}
	
	public void subtractItem(OfflinePlayer player, String itemName) {
		HashMap<String, Integer> playerItems = itemTables.get(player.getName());
		if (playerItems.containsKey(itemName)) {
			int itemCount = playerItems.get(itemName) - 1;
			if (itemCount > 0) {
				playerItems.put(itemName, itemCount);
				try {
					PreparedStatement statement = OakCoreLib.getDB().prepareStatement("UPDATE oakquests_keyitems SET count=? WHERE player=? AND item=?");
					statement.setInt(1, itemCount);
					statement.setString(2, player.getName());
					statement.setString(3, itemName);
					statement.executeUpdate();
					statement.close();
				} catch (SQLException e) {
					OakQuests.log.log(Level.SEVERE, e.getMessage());
				}
			} else {
				removeItemType(player, itemName);
			}
		}	
	}
	
	public int getItemCount(OfflinePlayer player, String itemName) {
		HashMap<String, Integer> playerItems = itemTables.get(player.getName());
		if (playerItems.containsKey(itemName)) {
			return playerItems.get(itemName);
		} else {
			return 0;
		}
	}
	
	public void removeItemType(OfflinePlayer player, String itemName) {
		HashMap<String, Integer> playerItems = itemTables.get(player.getName());
		if (playerItems.containsKey(itemName)) {
			playerItems.remove(itemName);
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakquests_keyitems WHERE player=? AND item=?");
				statement.setString(1, player.getName());
				statement.setString(2, itemName);
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				OakQuests.log.log(Level.SEVERE, e.getMessage());
			}
		}	
	}


	
}

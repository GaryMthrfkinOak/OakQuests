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
import java.util.Hashtable;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakquests.events.KeyItemAddedEvent;

public class KeyItems implements Listener {

	protected static Hashtable<Player, Hashtable<String, Integer>> itemTables;
	
	public KeyItems() {
		itemTables = new Hashtable<Player, Hashtable<String, Integer>>();
		for (Player player : OakQuests.server.getOnlinePlayers()) {
			itemTables.put(player, new Hashtable<String, Integer>());
		}
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT player, item, count FROM oakquests_keyitems;");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Player nextPlayer = OakQuests.server.getPlayerExact(result.getString(1));
				if (nextPlayer != null) {
					itemTables.get(nextPlayer).put(result.getString(2), result.getInt(3));
				}
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}

	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Hashtable<String, Integer> playerItems = new Hashtable<String, Integer>();
		itemTables.put(player, playerItems);
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT item, count FROM oakquests_keyitems WHERE player=?;");
			statement.setString(1, player.getName());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				playerItems.put(result.getString(1), result.getInt(2));
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (itemTables.containsKey(player)) {
			itemTables.get(player).clear();
			itemTables.remove(player);
		}
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (itemTables.containsKey(player)) {
			itemTables.get(player).clear();
			itemTables.remove(player);
		}
	}
	
	public void addItem(Player player, String itemName) {
		Hashtable<String, Integer> playerItems = itemTables.get(player);
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
	
	public void subtractItem(Player player, String itemName) {
		Hashtable<String, Integer> playerItems = itemTables.get(player);
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
	
	public int getItemCount(Player player, String itemName) {
		Hashtable<String, Integer> playerItems = itemTables.get(player);
		if (playerItems.containsKey(itemName)) {
			return playerItems.get(itemName);
		} else {
			return 0;
		}
	}
	
	public void removeItemType(Player player, String itemName) {
		Hashtable<String, Integer> playerItems = itemTables.get(player);
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

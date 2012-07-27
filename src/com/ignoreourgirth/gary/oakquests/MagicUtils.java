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
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakcorelib.StringFormats;

public class MagicUtils {

	public static String getSpellName(int spellID) {
		String returnValue = null;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT SpellName FROM oakmagic_spells WHERE SpellID=?;");
			statement.setInt(1, spellID);
			ResultSet result = statement.executeQuery();
			if (result.next()) returnValue = result.getString(1);
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakQuests.log.log(Level.WARNING, ex.getMessage());
		}
		return returnValue;
	}
	
	public static boolean knowsSpell(Player player, int spellID, int spellLevel) {
		boolean returnValue = false;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT SpellLevel FROM oakmagic_players WHERE SpellID=? AND PlayerName=?");
			statement.setInt(1, spellID);
			statement.setString(2, player.getName());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				if (result.getInt(1) == spellLevel) returnValue = true;	
			}
			result.close();
			statement.close();
		} catch (SQLException ex) {
			OakQuests.log.log(Level.WARNING, ex.getMessage());
		}
		return returnValue;
	}
	
	public static int spellsKnown(Player player) {
		int returnValue = 0;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT COUNT(*) FROM oakmagic_players WHERE PlayerName=?;");
			statement.setString(1, player.getName());
			ResultSet result = statement.executeQuery();
			if (result.next()) returnValue = result.getInt(1);
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
		return returnValue;
	}
	
	public static void teachSpell(String NPCName, Player player, int spellID, int spellLevel) {
		if (!knowsSpell(player, spellID, spellLevel)) {
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakmagic_players(SpellID, PlayerName, SpellLevel) VALUES(?, ?, ?)");
				statement.setInt(1, spellID);
				statement.setString(2, player.getName());
				statement.setInt(3, spellLevel);
				statement.executeUpdate();
				statement.close();
			} catch (SQLException ex) {
				OakQuests.log.log(Level.SEVERE, ex.getMessage());
			}
			player.sendMessage(ChatColor.LIGHT_PURPLE + NPCName + " taught you the spell " + ChatColor.GOLD + getSpellName(spellID) + " " + StringFormats.toRomanNumeral(spellLevel) + ChatColor.GREEN + ".");
		}
	}
	
	public static void setRecallLocation(Player player, Location location) {
		try {
			if (location == null) {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakmagic_markrecall WHERE player=?");
				statement.setString(1, player.getName());
				statement.executeUpdate();
				statement.close();
			} else {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("" +
						"INSERT INTO oakmagic_markrecall(player, world, x, y, z, yaw) VALUES (?,?,?,?,?,?)" +
						"ON DUPLICATE KEY UPDATE world=?, x=?, y=?, z=?, yaw=?");
				statement.setString(1, player.getName());
				statement.setString(2, location.getWorld().getName());
				statement.setDouble(3, location.getX());
				statement.setDouble(4, location.getY());
				statement.setDouble(5, location.getZ());
				statement.setFloat(6, location.getYaw());
				statement.setString(7, location.getWorld().getName());
				statement.setDouble(8, location.getX());
				statement.setDouble(9, location.getY());
				statement.setDouble(10, location.getZ());
				statement.setFloat(11, location.getYaw());
				statement.executeUpdate();
				statement.close();
			}
		} catch (SQLException ex) {
			OakQuests.log.log(Level.SEVERE, ex.getMessage());
		}
	}
	
}

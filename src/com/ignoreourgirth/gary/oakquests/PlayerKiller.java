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

import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;

public class PlayerKiller {

	public static void clearQuestKills(String questID, Player player) {
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakquests_playerkiller WHERE questID=? AND player=?;");
			statement.setString(1, questID);
			statement.setString(2, player.getName());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public static int getUniqueKills(String questID, Player player) {
		int kills = 0;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT COUNT(*) FROM oakquests_playerkiller WHERE questID=? AND player=?;");
			statement.setString(1, questID);
			statement.setString(2, player.getName());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				kills = result.getInt(1);
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
		return kills;
	}
	
	public static int getTimesKilled(String questID, Player player, Player victim) {
		int kills = 0;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT count FROM oakquests_playerkiller WHERE questID=? AND player=? AND victim=?;");
			statement.setString(1, questID);
			statement.setString(2, player.getName());
			statement.setString(3, victim.getName());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				kills = result.getInt(1);
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
		return kills;
	}
	
	public static void addPlayerKilled(String questID, Player player, Player victim) {
		try {
			if (getTimesKilled(questID, player, victim) > 0) {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("UPDATE oakquests_playerkiller SET count=count+1 WHERE questID=? AND player=? AND victim=?;");
				statement.setString(1, questID);
				statement.setString(2, player.getName());
				statement.setString(3, victim.getName());
				statement.executeUpdate();
				statement.close();
			} else {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakquests_playerkiller (questID, player, victim) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE count=count+1;");
				statement.setString(1, questID);
				statement.setString(2, player.getName());
				statement.setString(3, victim.getName());
				statement.executeUpdate();
				statement.close();
			}
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
	}
}

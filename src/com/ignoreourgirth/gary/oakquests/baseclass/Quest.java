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
package com.ignoreourgirth.gary.oakquests.baseclass;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.events.QuestDroppedEvent;

public abstract class Quest implements Listener  {

	private HashSet<Player> activePlayers;
	private Hashtable<Player, Integer> questStages;
	
	protected String questTitle;
	protected String questNPC;
	protected String questID;
	protected boolean isRepeatable = true;
	protected boolean isAbandonable = true;
	protected String unavailibleMessage = OakQuests.noQuestHereMessage;
	protected String notStartedTurnInMessage = OakQuests.noTurnInMessage;
	protected String alreadyActiveMessage = ChatColor.GRAY + "You still have a pending quest here.";
	protected String notRepeatbleMessage = ChatColor.GRAY + "This quest is not repeatable.";
	
	protected Location waypoint = null;
	
	protected boolean isOnQuest(Player player) {return activePlayers.contains(player); }
	protected int getQuestStage(Player player) {return questStages.get(player); }
	
	protected abstract void initialize();
	protected abstract void postInitialize();
	protected abstract void pluginUnload();
	protected abstract boolean isAvailable(Player player);
	protected abstract boolean turnIn(Player player);
	protected abstract void ask(Player player);
	protected abstract void accepted(Player player);
	protected abstract void denied(Player player);
	protected abstract void dropped(Player player);
	protected abstract void information(Player player);
	protected abstract Location waypoint(Player player);
	
	
	public String getQuestTitle() {return questTitle;}
	public String getNPCName() {return questNPC;}
	public String getID() {return questID;}

	public Quest() {
		activePlayers = new HashSet<Player>();
		questStages = new Hashtable<Player, Integer>();
		questID = "";
	}
	
	public void unloadClass() {
		pluginUnload();
	}
	
	public void initalizeClass() {
		initialize();
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT player, stage FROM oakquests_active WHERE questID=?;");
			statement.setString(1, questID);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Player nextPlayer = OakQuests.server.getPlayerExact(result.getString(1));
				if (nextPlayer != null) {
					activePlayers.add(nextPlayer);
					questStages.put(nextPlayer , result.getInt(2));
				}
			}
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
		postInitialize();
	}
	
	public void addActivePlayer(Player player, int stage) {
		if (!isOnQuest(player)) {
			activePlayers.add(player);
			questStages.put(player, stage);
		}
	}
	
	public void removeActivePlayer(Player player) {
		if (isOnQuest(player)) {
			activePlayers.remove(player);
			questStages.remove(player);
		}
	}
	
	public void getWaypoint(Player player) {
		if (isOnQuest(player)) {
			Location location = waypoint(player);
			if (location != null) {
				GeneralUtils.setWaypoint(player, location);
			} else {
				player.sendMessage(ChatColor.GRAY + "This quest does not have an active waypoint.");
			}
		} else {
			player.sendMessage(OakQuests.invalidIDMessage);
		}
	}
	
	public void askAboutQuest(Player player) {
		if (isAvailable(player) && !isOnQuest(player)) {
			if (!isRepeatable && OakQuests.loader.getCompletionCount(questID, player) > 0) {
				if (notRepeatbleMessage != null) player.sendMessage(notRepeatbleMessage);
				return;
			}
			ask(player);
		} else if (isOnQuest(player)) {
			if (alreadyActiveMessage != null) player.sendMessage(alreadyActiveMessage);
		} else {
			if (unavailibleMessage != null) player.sendMessage(unavailibleMessage);
		}
	}
	
	public void acceptQuest(Player player) {
		if (isAvailable(player) && !isOnQuest(player)) {
			if (!isRepeatable && OakQuests.loader.getCompletionCount(questID, player) > 0) {
				if (notRepeatbleMessage != null) player.sendMessage(notRepeatbleMessage);
				return;
			}
			accepted(player);
			activePlayers.add(player);
			questStages.put(player, 1);
			player.sendMessage(ChatColor.GOLD + "Started Quest: " + questTitle);
			try {
				PreparedStatement statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakquests_active (questID, player, stage) VALUES (?, ?, ?)");
				statement.setString(1, questID);
				statement.setString(2, player.getName());
				statement.setInt(3, 1);
				statement.executeUpdate();
				statement.close();
			} catch (SQLException e) {
				OakQuests.log.log(Level.SEVERE, e.getMessage());
			}
		} else if (isOnQuest(player)) {
			if (alreadyActiveMessage != null) player.sendMessage(alreadyActiveMessage);
		} else {
			if (unavailibleMessage != null) player.sendMessage(unavailibleMessage);
		}
	}
	
	public void denyQuest(Player player) {
		if (isAvailable(player) && !isOnQuest(player)) {
			if (!isRepeatable && OakQuests.loader.getCompletionCount(questID, player) > 0) {
				if (notRepeatbleMessage != null) player.sendMessage(notRepeatbleMessage);
				return;
			}
			denied(player);
		} else if (isOnQuest(player)) {
			if (alreadyActiveMessage != null) player.sendMessage(alreadyActiveMessage);
		} else {
			if (unavailibleMessage != null) player.sendMessage(unavailibleMessage);
		}
	}
	
	public void dropQuest(Player player) {
		if (isOnQuest(player)) {
			if (isAbandonable) {
				activePlayers.remove(player);
				questStages.remove(player);
				dropped(player);
				player.sendMessage(ChatColor.GRAY + "Dropped Quest: " + questTitle);
				try {
					PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakquests_active WHERE questID=? AND player=?");
					statement.setString(1, questID);
					statement.setString(2, player.getName());
					statement.executeUpdate();
					statement.close();
				} catch (SQLException e) {
					OakQuests.log.log(Level.SEVERE, e.getMessage());
				}
				OakQuests.server.getPluginManager().callEvent(new QuestDroppedEvent(player, this));
			} else {
				player.sendMessage(ChatColor.GRAY + "You are unable to drop this quest.");
			}
		} else {
			player.sendMessage(OakQuests.invalidIDMessage);
		}
	}
	
	public void finishQuest(Player player) {
		if (isOnQuest(player)) {
			if (turnIn(player)) {
				activePlayers.remove(player);
				questStages.remove(player);
				player.sendMessage(ChatColor.GOLD + "Finished Quest: " + questTitle);
				try {
					PreparedStatement statement = OakCoreLib.getDB().prepareStatement("DELETE FROM oakquests_active WHERE questID=? AND player=?");
					statement.setString(1, questID);
					statement.setString(2, player.getName());
					statement.executeUpdate();
					statement.close();
					statement = OakCoreLib.getDB().prepareStatement("INSERT INTO oakquests_completed (questID, player, date) VALUES (?, ?, ?)");
					statement.setString(1, questID);
					statement.setString(2, player.getName());
					statement.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
					statement.executeUpdate();
					statement.close();
				} catch (SQLException e) {
					OakQuests.log.log(Level.SEVERE, e.getMessage());
				}
			}
		} else {
			if (notStartedTurnInMessage != null) player.sendMessage(notStartedTurnInMessage);
		}
	}
	
	public void sendInfo(Player player) {
		if (isOnQuest(player)) {
			information(player);
		} else {
			player.sendMessage(OakQuests.invalidIDMessage);
		}
	}
	
}

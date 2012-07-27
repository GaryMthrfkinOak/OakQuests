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

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.ignoreourgirth.gary.oakcorelib.CommandPreprocessor.OnCommand;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class Commands {
	
	@OnCommand ("quest.ask")
	public void askCommand(Player player) {
		ArrayList<Quest> quests = OakQuests.loader.getNearbyQuests(player);
		if (quests != null) {
			for (Quest quest : quests) {
				quest.askAboutQuest(player);
			}
		} else {
			player.sendMessage(OakQuests.noQuestHereMessage);
		}
	}
	
	@OnCommand ("quest.accept")
	public void acceptCommand(Player player) {
		ArrayList<Quest> quests = OakQuests.loader.getNearbyQuests(player);
		if (quests != null) {
			for (Quest quest : quests) {
				quest.acceptQuest(player);
			}
		} else {
			player.sendMessage(OakQuests.noQuestHereMessage);
		}
	}
	
	@OnCommand ("quest.deny")
	public void denyCommand(Player player) {
		ArrayList<Quest> quests = OakQuests.loader.getNearbyQuests(player);
		if (quests != null) {
			for (Quest quest : quests) {
				quest.denyQuest(player);
			}
		} else {
			player.sendMessage(OakQuests.noQuestHereMessage);
		}
	}
	
	@OnCommand ("quest.finish")
	public void finishCommand(Player player) {
		ArrayList<Quest> quests = OakQuests.loader.getNearbyQuests(player);
		if (quests != null) {
			for (Quest quest : quests) {
				quest.finishQuest(player);
			}
		} else {
			player.sendMessage(OakQuests.noTurnInMessage);
		}
	}
	
	@OnCommand ("quest.list")
	public void listCommand(Player player) {
		ArrayList<Quest> activeQuests = OakQuests.loader.getActiveQuests(player);
		if (activeQuests.size() > 0) {
			player.sendMessage(ChatColor.GRAY + "----- Active Quests -----");
		} else {
			player.sendMessage(ChatColor.GRAY + "You have no active quests.");
		}
		int iteration = 0;
		for (Quest quest : activeQuests) {
			iteration++;
			StringBuilder message = new StringBuilder();
			message.append(ChatColor.GRAY + (iteration + ". "));
			message.append(quest.getQuestTitle());
			message.append(ChatColor.GOLD + " (" + quest.getID() + ")");
			player.sendMessage(message.toString());
		}
	}
	
	@OnCommand (value="quest.drop", labels="QuestID")
	public void dropCommand(Player player, String ID) {
		Quest quest = OakQuests.loader.getQuest(ID);
		if (quest != null) {
			quest.dropQuest(player);
		} else {
			player.sendMessage(OakQuests.invalidIDMessage);
		}
	}
	
	@OnCommand (value="quest.info", labels="QuestID")
	public void infoCommand(Player player, String ID) {
		Quest quest = OakQuests.loader.getQuest(ID);
		if (quest != null) {
			quest.sendInfo(player);
		} else {
			player.sendMessage(OakQuests.invalidIDMessage);
		}
	}
	
	@OnCommand ("waypoint.clear")
	public void waypointClear(Player player) {
		GeneralUtils.clearWaypoint(player);
	}
	
	@OnCommand ("waypoint.here")
	public void waypointHere(Player player) {
		GeneralUtils.setWaypoint(player, player.getLocation());
	}
	
	@OnCommand (value="waypoint.quest", labels="QuestID")
	public void waypointQuest(Player player, String ID) {
		Quest quest = OakQuests.loader.getQuest(ID);
		if (quest != null) {
			quest.getWaypoint(player);
		} else {
			player.sendMessage(OakQuests.invalidIDMessage);
		}
	}
	
	@OnCommand (value="waypoint.custom", labels="WorldName, X, Y, Z")
	public void waypointCustom(Player player, String worldArg, String xArg, String yArg, String zArg) {
		try {
			worldArg = worldArg.replace(",", "");
			xArg = xArg.replace(",", "");
			yArg = yArg.replace(",", "");
			zArg = yArg.replace(",", "");
			World world = OakQuests.server.getWorld(worldArg);
			if (world != null) {
				int x = Integer.parseInt(xArg);
				int y = Integer.parseInt(yArg);
				int z = Integer.parseInt(zArg);
				Location waypointLocation = new Location(world, x, y, z);
				GeneralUtils.setWaypoint(player, waypointLocation);
			} else {
				player.sendMessage(ChatColor.RED + "§4A world with tha name does not exist.");
			}
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "§4Invalid argument(s) : Expected Integer");
		}
	}
	
	@OnCommand ("keyitems")
	public void keyItemCommand(Player player){
		player.sendMessage(ChatColor.GRAY + "---- Key Items ---- ");
		int iteration = 0;
		for (Map.Entry<String, Integer> entry : KeyItems.itemTables.get(player).entrySet()) {
			iteration ++;
			player.sendMessage(ChatColor.GRAY + (iteration + ". ") + ChatColor.GOLD + entry.getKey() + ChatColor.WHITE + " (" + entry.getValue() + ")");
		}

	}

}

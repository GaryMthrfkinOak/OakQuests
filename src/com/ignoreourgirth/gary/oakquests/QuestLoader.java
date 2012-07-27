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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;
import com.ignoreourgirth.gary.oakquests.quests.ArchersAnonymous1;
import com.ignoreourgirth.gary.oakquests.quests.ArchersAnonymous2;
import com.ignoreourgirth.gary.oakquests.quests.ArchersAnonymous3;
import com.ignoreourgirth.gary.oakquests.quests.TheHermetPart1;
import com.ignoreourgirth.gary.oakquests.quests.ScandiumRepeatable;
import com.ignoreourgirth.gary.oakquests.quests.LavaLava;
import com.ignoreourgirth.gary.oakquests.quests.HolySpellQuest;
import com.ignoreourgirth.gary.oakquests.quests.SpidersRepeatable;
import com.ignoreourgirth.gary.oakquests.quests.TheHermetPart2;
import com.ignoreourgirth.gary.oakquests.quests.TheHermetPart3;

public class QuestLoader implements Listener {

	private ArrayList<Class<? extends Quest>> activeClasses;
	private HashSet<Quest> loadedQuests;
	private Hashtable<String, Quest> questByID;
	private Hashtable<String, ArrayList<Quest>> questByNPCName;
	private Set<String> npcNames;
	
	public QuestLoader() {
		activeClasses = new ArrayList<Class<? extends Quest>>();
		questByID = new Hashtable<String, Quest>();
		questByNPCName = new Hashtable<String, ArrayList<Quest>>();
		loadedQuests = new HashSet<Quest>();
		compileClassList();
		loadQuests();
		npcNames = questByNPCName.keySet();
	}
	
	private void compileClassList() {
		activeClasses.add(LavaLava.class);
		activeClasses.add(HolySpellQuest.class);
		activeClasses.add(ScandiumRepeatable.class);
		activeClasses.add(SpidersRepeatable.class);
		activeClasses.add(TheHermetPart1.class);
		activeClasses.add(TheHermetPart2.class);
		activeClasses.add(TheHermetPart3.class);
		activeClasses.add(ArchersAnonymous1.class);
		activeClasses.add(ArchersAnonymous2.class);
		activeClasses.add(ArchersAnonymous3.class);
	}
	
	private void loadQuests() {
		try {
			
			for (Class<? extends Quest> questClass : activeClasses) {
    			Constructor<? extends Quest> constructor = questClass.getConstructor();
    			Quest instancedClass = (Quest) constructor.newInstance();
    			instancedClass.initalizeClass();
    			OakQuests.server.getPluginManager().registerEvents(instancedClass, OakQuests.plugin);
    			loadedQuests.add(instancedClass);
    			questByID.put(instancedClass.getID().toLowerCase(), instancedClass);
    			String npc = instancedClass.getNPCName();
    			if (!questByNPCName.containsKey(npc)) questByNPCName.put(npc, new ArrayList<Quest>());
    			questByNPCName.get(npc).add(instancedClass);
			}
		} catch (InstantiationException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		} catch (IllegalAccessException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		} catch (SecurityException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		} catch (NoSuchMethodException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		} catch (IllegalArgumentException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		} catch (InvocationTargetException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public void unloadQuests() {
		for (Quest quest : loadedQuests) {
			quest.unloadClass();
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT questID, stage FROM oakquests_active WHERE player=?;");
			statement.setString(1, player.getName());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				questByID.get(result.getString(1).toLowerCase()).addActivePlayer(player, result.getInt(2));
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
		for (Map.Entry<String, Quest> entry : questByID.entrySet()) { 
			entry.getValue().removeActivePlayer(player);
		}
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		for (Map.Entry<String, Quest> entry : questByID.entrySet()) { 
			entry.getValue().removeActivePlayer(player);
		}
	}
	
	public Quest getQuest(String ID) {
		ID = ID.toLowerCase();
		if (questByID.containsKey(ID)) {
			return questByID.get(ID);
		} else {
			return null;
		}
	}
	
	public ArrayList<Quest> getNearbyQuests(Player player) {
		for(Entity entity : player.getNearbyEntities(7, 4, 7))
		{
		    if (entity instanceof HumanEntity) {
		    	for (String NPCName : npcNames) {
		    		if (((HumanEntity) entity).getName().equalsIgnoreCase(NPCName)) return questByNPCName.get(NPCName);
		    	}
		    }
		    	
		}
		return null;
	}
	
	public ArrayList<Quest> getActiveQuests(Player player) {
		ArrayList<Quest> returnValue = new ArrayList<Quest>();
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT questID FROM oakquests_active WHERE player=?;");
			statement.setString(1, player.getName());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				String nextID = result.getString(1).toLowerCase();
				if (questByID.containsKey(nextID)){
					returnValue.add(questByID.get(nextID));
				}
			}
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
		return returnValue;	
	}
	
	public int getCompletionCount(String ID, Player player) {
		ID = ID.toLowerCase();
		int returnValue = 0;
		try {
			PreparedStatement statement = OakCoreLib.getDB().prepareStatement("SELECT COUNT(*) FROM oakquests_completed WHERE questID=? AND player=?;");
			statement.setString(1, ID);
			statement.setString(2, player.getName());
			ResultSet result = statement.executeQuery();
			if (result.next()) returnValue = result.getInt(1);
			result.close();
			statement.close();
		} catch (SQLException e) {
			OakQuests.log.log(Level.SEVERE, e.getMessage());
		}
		return returnValue;	
	}
	
}

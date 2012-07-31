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

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.ignoreourgirth.gary.oakcorelib.CommandPreprocessor;
import com.ignoreourgirth.gary.oakcorelib.DisplayItems;
import com.ignoreourgirth.gary.oakcorelib.ProximityDetection;

public class OakQuests extends JavaPlugin {
	
	public static final String usageMessage = ChatColor.AQUA + "Usage: /quest [ask/accept/deny/drop/finish/list/info]";
	public static final String invalidIDMessage = ChatColor.GRAY + "Invalid quest ID. Use '/quest list' to get ID.";
	public static final String noQuestHereMessage = ChatColor.GRAY + "There are no available quests here.";
	public static final String noTurnInMessage = ChatColor.GRAY + "There is no quest turn-in here.";
	
	public static Logger log;
	public static Plugin plugin;
	public static Server server;
	public static QuestLoader loader;
	public static KeyItems keyItems;
	
	public void onEnable() {
		log = this.getLogger();
		plugin = this;
		server = this.getServer();
        keyItems = new KeyItems();
        loader = new QuestLoader();
        OakQuests.server.getPluginManager().registerEvents(loader, plugin);
        OakQuests.server.getPluginManager().registerEvents(keyItems, plugin);
        CommandPreprocessor.addExecutor(new Commands());
	}
	
	public void onDisable() {
		loader.unloadQuests();
		DisplayItems.removeAll(this);
		ProximityDetection.removeAll(this);
	}
	
}

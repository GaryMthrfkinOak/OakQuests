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

import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.ignoreourgirth.gary.oakquests.baseclass.Quest;
import com.ignoreourgirth.gary.oakquests.events.QuestDroppedEvent;

public class KeyItemEntity implements Listener {
	
	private String name;
	private Player player;
	private Material material;
	private Location location;
	private Item item;
	private Quest quest;
	private boolean disposed;
	private HashSet<Location> protectedLocations;
	private int taskID;
	
	public KeyItemEntity(Location spawnLocation, String keyItemName, Material itemMaterial) {
		this(null, null, spawnLocation, keyItemName, itemMaterial);
	}
	
	public KeyItemEntity(Quest relatedQuest, Player questPlayer, Location spawnLocation, String keyItemName, Material itemMaterial) {
		OakQuests.activeItems.add(this);
		quest = relatedQuest;
		player = questPlayer;
		name = keyItemName;
		material = itemMaterial;
		location = spawnLocation;
		protectedLocations = new HashSet<Location>();
		for (int x=-1; x<2; x++) {
			for (int y=-1; y<2; y++) {
				for (int z=-1; z<2; z++) {
					protectedLocations.add(location.clone().add(new Vector(x, y, z)).getBlock().getLocation());
				}
			}
		}
		OakQuests.server.getPluginManager().registerEvents(this, OakQuests.plugin);
		taskID = OakQuests.server.getScheduler().scheduleSyncRepeatingTask(OakQuests.plugin, new Runnable() {
    		public void run() {
    			if (item == null) {
    				if (location.getChunk().isLoaded()) spawnItem();
    			} else if (item.isDead()) {
    				if (location.getChunk().isLoaded()) spawnItem();
    			} else {
    				item.setTicksLived(20);
    			}
    		}
    	}, 0, 2400);
	}
	
	public void dispose() {
		if (!disposed) {
			disposed = true;
			despawnItem();
			HandlerList.unregisterAll(this);
			OakQuests.server.getScheduler().cancelTask(taskID);
			OakQuests.activeItems.remove(this);
		}
	}
	
	@EventHandler 
	public void onQuestDropped(QuestDroppedEvent event) {
		if (quest == event.getQuest() && player == event.getPlayer()) {
			dispose();
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent event) {
		if (event.getChunk().equals(location.getChunk())) spawnItem();
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (event.getChunk().equals(location.getChunk())) despawnItem();
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (protectedLocations.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (protectedLocations.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		Item pickedUp = event.getItem();
		if (item != null) {
			if (pickedUp.equals(item)) {
				event.setCancelled(true);
				if (player == null || player == event.getPlayer())  {
					player.sendMessage(ChatColor.GRAY + "Picked up " + ChatColor.GOLD + name + ChatColor.GRAY + ".");
					OakQuests.keyItems.addItem(player, name);
					dispose();
				}
			}
		}
	}
	
	private void spawnItem() {
		boolean shouldSpawn = false;
		if (item == null) {
			shouldSpawn = true;
		} else if (item.isDead()) {
			shouldSpawn = true;
		}
		if (shouldSpawn) {
			item = location.getWorld().dropItem(location, new ItemStack(material));
		    item.setPickupDelay(0);
		    item.setVelocity(new Vector(0,0,0));
		}
	}
	
	private void despawnItem() {
		if (item != null) {
			item.remove();
			item = null;
		}
	}
	
}

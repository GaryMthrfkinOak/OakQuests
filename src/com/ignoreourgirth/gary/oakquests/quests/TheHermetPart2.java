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
package com.ignoreourgirth.gary.oakquests.quests;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.KeyItemEntity;
import com.ignoreourgirth.gary.oakquests.MagicUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;
import com.ignoreourgirth.gary.oakquests.events.KeyItemAddedEvent;

public class TheHermetPart2 extends Quest {

	private final String orbItemName = "Power Orb";
	private final Location waypointLocation = new Location(OakQuests.server.getWorld("new_world"), 1538.1289, 73, 3281.6412);
	private final Location orbLocation = new Location(OakQuests.server.getWorld("new_world"), 1523.5158, 59, 3309.4891);
	private final Location hermetLocation = new Location(OakQuests.server.getWorld("new_world"), 1548.2684, 67, 3204.4282, -88.2f, 0f);
	
	private Location doorBlock1 =  new Location(OakQuests.server.getWorld("new_world"), 1523, 58, 3294);
	private Location doorBlock2 =  new Location(OakQuests.server.getWorld("new_world"), 1523, 57, 3294);
	private Location roomCenter =  new Location(OakQuests.server.getWorld("new_world"), 1523, 57, 3303);
	private HashSet<LivingEntity> spawnedMobs;
	private int mobsRemaining;
	
	@Override
	public void initialize() {
		questID = "Hermet2";
		questTitle = "The Hermet, Part II";
		questNPC = "Theo";
		isRepeatable = false;
		unavailibleMessage = null;
		notRepeatbleMessage = null;
		notStartedTurnInMessage = null;
		spawnedMobs = new HashSet<LivingEntity>();
	}

	@Override
	protected void postInitialize() {
		for (Player player : OakQuests.server.getOnlinePlayers()) {
			if (isOnQuest(player)) {
				if ((OakQuests.keyItems.getItemCount(player, orbItemName) == 0)) {
					new KeyItemEntity(this, player, orbLocation, orbItemName, Material.MAGMA_CREAM);
				}
			}
		}
		endLockIn();
	}
	
	@Override
	protected void pluginUnload() {
		endLockIn();
	}
	
	@Override
	protected boolean isAvailable(Player player) {
		return (OakQuests.loader.getCompletionCount("Hermet1", player) > 0);
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Ju did goowd. Ju get me ohb two. Ja?");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Svist une ist close.");
		GeneralUtils.setWaypoint(player, waypointLocation);
		MagicUtils.setRecallLocation(player, hermetLocation);
		new KeyItemEntity(this, player, orbLocation, orbItemName, Material.MAGMA_CREAM);
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Zvat? I need svee ohb.");
	}

	@Override
	protected void dropped(Player player) {
		 OakQuests.keyItems.removeItemType(player, orbItemName);
	}

	@Override
	protected void information(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
			player.sendMessage(ChatColor.WHITE + "You have the second orb. Use " + ChatColor.GOLD  + "recall " + ChatColor.WHITE + "to return to " + ChatColor.GOLD  + questNPC + ChatColor.WHITE + ".");
		} else {
			player.sendMessage(ChatColor.WHITE + "The hermet named " +  ChatColor.GOLD  + questNPC + ChatColor.WHITE + " wanted another orb.");
			GeneralUtils.setWaypoint(player, waypointLocation);
		}
	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
			GeneralUtils.sendFromNPC(player, questNPC,  "Das ist ausgezeichnet! Oonly one moore.");
			OakQuests.keyItems.removeItemType(player, orbItemName);
			CommonRewards.giveXP(player, 100);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Sver ist zvee sekont ohb?");
			return false;
		}
	}
	
	@Override
	protected Location waypoint(Player player) {
		if (OakQuests.keyItems.getItemCount(player, orbItemName) == 0) {
			return waypointLocation;
		} else {
			return hermetLocation;
		}
	}
	
	@EventHandler
	public void onKeyItemAdded(KeyItemAddedEvent event) {
		Player player = event.getPlayer();
		if (isOnQuest(player)) {
			if (event.getItemName().equals(orbItemName)) {
				player.sendMessage(ChatColor.WHITE + "You have the second orb. Use " + ChatColor.GOLD  + "recall " + ChatColor.WHITE + "to return to " + ChatColor.GOLD  + questNPC + ChatColor.WHITE + ".");
				startLockIn(player);
				MagicUtils.setRecallLocation(player, hermetLocation);
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (spawnedMobs.contains(entity)) {
			spawnedMobs.remove(entity);
			mobsRemaining -= 1;
			if (mobsRemaining == 0) {
				endLockIn();
			}
		} else if (entity instanceof Player) {
			Player player = (Player) entity;
			if (isOnQuest(player)) {
				if (OakQuests.keyItems.getItemCount(player, orbItemName) > 0) {
					OakQuests.keyItems.removeItemType(player, orbItemName);
					new KeyItemEntity(this, player, orbLocation, orbItemName, Material.MAGMA_CREAM);
				}
			}
		}
	}
	
	private void endLockIn() {
		doorBlock1.getBlock().setType(Material.AIR);
		doorBlock2.getBlock().setType(Material.AIR);
		doorBlock1.getWorld().playEffect(doorBlock1, Effect.DOOR_TOGGLE, 0);
		for (LivingEntity entity : spawnedMobs) entity.remove();
	}
	
	private void startLockIn(final Player player) {
		final World baseWorld = doorBlock1.getWorld();
		Random randomGen = new Random();
		int radius = 5;
		int totalMobs = 20;
		int baseDelay = 20;
		double baseX = roomCenter.getX();
		double baseZ = roomCenter.getZ();
		doorBlock1.getBlock().setType(Material.IRON_BLOCK);
		doorBlock2.getBlock().setType(Material.IRON_BLOCK);
		doorBlock1.getWorld().playEffect(doorBlock1, Effect.DOOR_TOGGLE, 0);
		mobsRemaining += totalMobs;
        for (int loop = 0; loop < totalMobs; loop++) {
        	int nextDelay = (int) (baseDelay + (loop * 30) + (randomGen.nextDouble() * 400));
        	double XModifier = randomGen.nextDouble() * radius;
        	double ZModifier = randomGen.nextDouble() * radius;
        	if (randomGen.nextDouble() > .5) XModifier *= -1;
        	if (randomGen.nextDouble() > .5) ZModifier *= -1;
        	final Location nextLocation = new Location(baseWorld, baseX + XModifier, 58, baseZ + ZModifier, (float) (randomGen.nextDouble() * 360), 0f);
        	OakQuests.server.getScheduler().scheduleSyncDelayedTask(OakQuests.plugin, new Runnable() {
        		public void run() {
        			spawnedMobs.add(baseWorld.spawnCreature(nextLocation, EntityType.ZOMBIE));
        			baseWorld.playEffect(nextLocation, Effect.MOBSPAWNER_FLAMES, 0);
        			baseWorld.playEffect(nextLocation, Effect.CLICK1, 0);
        		}
        	}, nextDelay);
        	OakQuests.server.getScheduler().scheduleSyncDelayedTask(OakQuests.plugin, new Runnable() {
        		public void run() {
        			endLockIn();
        		}
        	}, 4800);
        } 
	}
	
	

	
}

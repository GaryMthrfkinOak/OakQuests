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

import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ignoreourgirth.gary.oakcorelib.OakCoreLib;
import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.MagicUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class ArchersAnonymous1 extends Quest {

	private int barrageID = 55;
	private int itemsNeeded = 50;
	private String keyItemName = "Blood-Stained Arrows";
	private Hashtable<LivingEntity, Player> shotEntities;
	
	@Override
	public void initialize() {
		questID = "Archer1";
		questTitle = "Archers Anonymous I";
		questNPC = "HeterosexualElve";
		isRepeatable = false;
		unavailibleMessage = null;
		notRepeatbleMessage = null;
		notStartedTurnInMessage = null;
		shotEntities = new Hashtable<LivingEntity, Player>();
	}
	
	@Override
	protected void postInitialize() {	
	}
	
	@Override
	protected void pluginUnload() {
	}

	@Override
	protected boolean isAvailable(Player player) {
		return true;
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Would you like to learn how to notch an arrow? Teehee.");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Fabulous! I mean great. Take a bow and give em' hell! Kill everything that moves. Come back to me when you're a MAN!");
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity defender = (LivingEntity) event.getEntity();
			Player killer = defender.getKiller();
			if (killer != null) {
				if (isOnQuest(killer)) {
					if (event.getDamager() instanceof Arrow) {
						if (!OakCoreLib.isFromMobSpawner(defender)) shotEntities.put(defender, killer);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (shotEntities.containsKey(event.getEntity())) {
			Player player = shotEntities.get(event.getEntity());
			if (player == event.getEntity().getKiller()) {
				OakQuests.keyItems.addItem(player, keyItemName);
				int itemCount = OakQuests.keyItems.getItemCount(player, keyItemName);
				if (itemCount == itemsNeeded) {
					player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the... uhm... " + ChatColor.GOLD  + questNPC);
				} else if (itemCount % 10 == 0) {
					player.sendMessage(ChatColor.GRAY + "Quest Update: " + (itemsNeeded - itemCount) + " arrow laden corpse(s) to go.");
				}
			}
		}
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Your loss sweetie.");
	}

	@Override
	protected void dropped(Player player) {
		 OakQuests.keyItems.removeItemType(player, keyItemName);
	}

	@Override
	protected void information(Player player) {
		int itemCount = OakQuests.keyItems.getItemCount(player, keyItemName);
		if (itemCount >= itemsNeeded) {
			player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the... uhm... " + ChatColor.GOLD  + questNPC);
		} else {
			player.sendMessage(ChatColor.WHITE + "Kill with a bow and arrow. " + ChatColor.GOLD  + (itemsNeeded - itemCount) + ChatColor.WHITE + " arrow laden corpse(s) to go.");
		}

	}
	
	@Override
	protected boolean turnIn(Player player) {
		if (OakQuests.keyItems.getItemCount(player, keyItemName) >= itemsNeeded) {
			GeneralUtils.sendFromNPC(player, questNPC,  "Christ! You're covered in blood... Just the way I like it.");
			OakQuests.keyItems.removeItemType(player, keyItemName);
			CommonRewards.giveMoney(player, 25000);
			MagicUtils.teachSpell(questNPC, player, barrageID, 1);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Honey, you're still a boy. Go shoot some more bad guys.");
			return false;
		}
	}

	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

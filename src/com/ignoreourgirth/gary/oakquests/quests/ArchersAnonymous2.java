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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ignoreourgirth.gary.oakquests.CommonRewards;
import com.ignoreourgirth.gary.oakquests.GeneralUtils;
import com.ignoreourgirth.gary.oakquests.MagicUtils;
import com.ignoreourgirth.gary.oakquests.OakQuests;
import com.ignoreourgirth.gary.oakquests.PlayerKiller;
import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class ArchersAnonymous2 extends Quest {

	private int barrageID = 55;
	private int killsNeeded = 4;
	private Hashtable<Player, Player> shotPlayers;
	
	@Override
	public void initialize() {
		questID = "Archer2";
		questTitle = "Archers Anonymous II";
		questNPC = "Hetero Elve";
		isRepeatable = false;
		unavailibleMessage = null;
		notRepeatbleMessage = null;
		notStartedTurnInMessage = null;
		shotPlayers = new Hashtable<Player, Player>();
	}
	
	@Override
	protected void postInitialize() {	
	}
	
	@Override
	protected void pluginUnload() {
	}

	@Override
	protected boolean isAvailable(Player player) {
		return (OakQuests.loader.getCompletionCount("Archer1", player) > 0);
	}

	@Override
	protected void ask(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Back for more big boy?");
	}

	@Override
	protected void accepted(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "This time I want you to kill some people for me. Can you do that sugarpie? <3");
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player defender = (Player) event.getEntity();
			Player killer = defender.getKiller();
			if (killer != null) {
				if (isOnQuest(killer)) {
					if (event.getDamager() instanceof Arrow) {
						shotPlayers.put(defender, killer);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (shotPlayers.containsKey(event.getEntity())) {
			Player victim = (Player) event.getEntity();
			Player player = shotPlayers.get(event.getEntity());
			if (player == event.getEntity().getKiller()) {
				PlayerKiller.addPlayerKilled(questID, player, victim);
				int uniqueKills = PlayerKiller.getUniqueKills(questID, player);
				if (uniqueKills == killsNeeded) {
					player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the... uhm... " + ChatColor.GOLD  + questNPC);
				} else {
					player.sendMessage(ChatColor.GRAY + "Quest Update: " + (killsNeeded - uniqueKills) + " unique kill(s) to go.");
				}
			}
		}
	}

	@Override
	protected void denied(Player player) {
		GeneralUtils.sendFromNPC(player, questNPC,  "Aw, come on baby.");
	}

	@Override
	protected void dropped(Player player) {
		PlayerKiller.clearQuestKills(questID, player);
	}

	@Override
	protected void information(Player player) {
		int uniqueKills = PlayerKiller.getUniqueKills(questID, player);
		if (uniqueKills >= killsNeeded) {
			player.sendMessage(ChatColor.WHITE + "Quest task complete. Return to the... uhm... " + ChatColor.GOLD  + questNPC);
		} else {
			player.sendMessage(ChatColor.WHITE + "Kill players with a bow and arrow. " + ChatColor.GOLD  + (killsNeeded - uniqueKills) + ChatColor.WHITE + " unique kill(s) to go.");
		}

	}
	
	@Override
	protected boolean turnIn(Player player) {
		int uniqueKills = PlayerKiller.getUniqueKills(questID, player);
		if (uniqueKills >= killsNeeded) {
			GeneralUtils.sendFromNPC(player, questNPC,  "You're my favorite!");
			PlayerKiller.clearQuestKills(questID, player);
			CommonRewards.giveMoney(player, 10000);
			MagicUtils.teachSpell(questNPC, player, barrageID, 2);
			return true;
		} else {
			GeneralUtils.sendFromNPC(player, questNPC,  "Go kill me some more bad men. <3");
			return false;
		}
	}

	@Override
	protected Location waypoint(Player player) {
		return null;
	}
	
}

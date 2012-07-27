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
package com.ignoreourgirth.gary.oakquests.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ignoreourgirth.gary.oakquests.baseclass.Quest;

public class QuestDroppedEvent extends Event {

		private static final HandlerList handlers = new HandlerList();
		
		private Player eventPlayer;
		private Quest eventQuest;
		
		public Player getPlayer() {return eventPlayer;}
		public Quest getQuest() {return eventQuest;}
		
	    public QuestDroppedEvent(Player player, Quest quest) {
	    	eventPlayer = player;
	    	eventQuest = quest;
	    }
	 
	    @Override
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
}

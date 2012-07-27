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

public class KeyItemAddedEvent extends Event {

		private static final HandlerList handlers = new HandlerList();
		
		private Player eventPlayer;
		private String eventItemName;
		private int eventTotal;
		
		public Player getPlayer() {return eventPlayer;}
		public String getItemName() {return eventItemName;}
		public int getTotalItems() {return eventTotal;}
		
	    public KeyItemAddedEvent(Player player, String itemName, int total) {
	    	eventPlayer = player;
	    	eventItemName = itemName;
	    	eventTotal = total;
	    }
	 
	    @Override
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
}

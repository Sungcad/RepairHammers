/*
 * Copyright (C) 2020  Sungcad
 */
package me.sungcad.repairhammers.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import me.sungcad.repairhammers.hammers.Hammer;

public class HammerUseEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();

	boolean cancelled;
	final Hammer hammer;
	final Player player;
	int target;
	final Inventory inventory;

	public HammerUseEvent(Hammer hammer, Player player, int target) {
		this(hammer, player, target, player.getInventory());
	}

	public HammerUseEvent(Hammer hammer, Player player, int target, Inventory inventory) {
		cancelled = false;
		this.hammer = hammer;
		this.player = player;
		this.target = target;
		this.inventory = inventory;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancell) {
		cancelled = cancell;
	}

	public Hammer getHammer() {
		return hammer;
	}

	public Player getPlayer() {
		return player;
	}

	public int getTargetedSlot() {
		return target;
	}

	public void setTargetedSlot(int slot) {
		target = slot;
	}

	public Inventory getInventory() {
		return inventory;
	}
}

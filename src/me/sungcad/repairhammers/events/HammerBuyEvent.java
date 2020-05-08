package me.sungcad.repairhammers.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.sungcad.repairhammers.hammers.Hammer;

public class HammerBuyEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();

	boolean cancelled;
	Hammer hammer;
	Player player;

	public HammerBuyEvent(Hammer hammer, Player player) {
		this.hammer = hammer;
		this.player = player;
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

}

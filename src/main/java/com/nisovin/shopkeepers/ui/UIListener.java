package com.nisovin.shopkeepers.ui;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.nisovin.shopkeepers.Log;
import com.nisovin.shopkeepers.Utils;

class UIListener implements Listener {

	private final UIManager uiManager;

	UIListener(UIManager uiManager) {
		this.uiManager = uiManager;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer().getType() != EntityType.PLAYER) return;
		Player player = (Player) event.getPlayer();
		UISession session = uiManager.getSession(player);
		if (session != null) {
			Log.debug("Player " + player.getName() + " closed " + session.getUIType().getIdentifier());
			// inform uiManager so that it can cleanup player data:
			uiManager.onInventoryClose(player);
			// inform uiHandler so that it can react to it:
			if (session.getUIHandler().isWindow(event.getInventory())) {
				session.getUIHandler().onInventoryClose(event, player);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked().getType() != EntityType.PLAYER) return;
		Player player = (Player) event.getWhoClicked();
		UISession session = uiManager.getSession(player);
		if (session != null) {
			// inform uiHandler so that it can react to it:
			if (session.getUIHandler().isWindow(event.getInventory())) {
				// debug information:
				Log.debug("Player " + player.getName() + " clicked: raw slot id=" + event.getRawSlot() + ", slot id=" + event.getSlot()
						+ ", slot type=" + event.getSlotType().name() + ", shift=" + event.isShiftClick()
						+ ", left or right=" + (event.isLeftClick() ? "left" : (event.isRightClick() ? "right" : "unknown"))
						+ ", action=" + event.getAction().name());

				session.getUIHandler().onInventoryClick(event, player);
			} else {
				// the player probably has some other inventory open, but an active session.. let's close it
				event.setCancelled(true);
				uiManager.onInventoryClose(player); // cleanup
				Utils.closeInventoryLater(player);
			}
		}
	}
}
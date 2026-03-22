package it.staffplugin.listeners;

import it.staffplugin.StaffPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InvseeListener implements Listener {

    private final StaffPlugin plugin;

    public InvseeListener(StaffPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;

        if (!viewer.hasPermission("staffplugin.staff.invsee")) return;

        if (event.getInventory().getHolder() instanceof Player target && !target.equals(viewer)) {

            if (event.getRawSlot() < event.getInventory().getSize()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;
        if (!viewer.hasPermission("staffplugin.staff.invsee")) return;

        if (event.getInventory().getHolder() instanceof Player target && !target.equals(viewer)) {
            event.setCancelled(true);
        }
    }
}

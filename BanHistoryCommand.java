package it.staffplugin.gui;

import it.staffplugin.StaffPlugin;
import it.staffplugin.utils.GuiUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeGui {

    public static final String HOME_GUI_TITLE_PREFIX = "§8» §bHome §8«";
    public static final String CONFIRM_DELETE_PREFIX = "§cConferma eliminazione: ";

    public static void open(StaffPlugin plugin, Player player) {
        int maxHomes = plugin.getHomeManager().getMaxHomes(player);
        Map<String, Location> homes = plugin.getHomeManager().getHomes(player.getUniqueId());

        int rows = Math.max(3, Math.min(6, (int) Math.ceil((maxHomes + 9) / 9.0) + 1));
        Inventory inv = GuiUtils.createGui(plugin.getConfig().getString("home.menu-title", HOME_GUI_TITLE_PREFIX), rows);
        GuiUtils.fillBorder(inv);

        List<String> homeNames = new ArrayList<>(homes.keySet());
        int slot = 10;
        int count = 0;
        for (int i = 0; i < maxHomes; i++) {
            if (slot % 9 == 8) slot += 2; 
            if (slot >= inv.getSize() - 9) break;

            if (i < homeNames.size()) {
                String homeName = homeNames.get(i);
                Location loc = homes.get(homeName);
                List<String> lore = new ArrayList<>();
                lore.add("§7Mondo: §e" + loc.getWorld().getName());
                lore.add("§7X: §e" + (int) loc.getX() + " §7Y: §e" + (int) loc.getY() + " §7Z: §e" + (int) loc.getZ());
                lore.add("");
                lore.add("§aClicca §7per teletrasportarti");
                lore.add("§cShift+Click §7per eliminare");
                inv.setItem(slot, GuiUtils.createItem(Material.BED, "§6" + homeName, lore));
            } else {

                List<String> lore = new ArrayList<>();
                lore.add("§7Slot libero");
                lore.add("");
                lore.add("§eClicca §7per impostare una home qui");
                inv.setItem(slot, GuiUtils.createItem(Material.LIGHT_GRAY_BED, "§7Slot Libero §8(§7" + (i + 1) + "§8)", lore));
            }
            slot++;
            count++;
        }

        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Home usate: §e" + homeNames.size() + "§7/§e" + maxHomes);
        infoLore.add("§7Usa §e/sethome <nome> §7per creare una home");
        inv.setItem(inv.getSize() - 5, GuiUtils.createItem(Material.COMPASS, "§6Le tue Home", infoLore));

        inv.setItem(inv.getSize() - 1, GuiUtils.createItem(Material.BARRIER, "§cChiudi"));

        player.openInventory(inv);
    }

    public static void openConfirmDelete(StaffPlugin plugin, Player player, String homeName) {
        Inventory inv = GuiUtils.createGui(CONFIRM_DELETE_PREFIX + homeName, 3);
        GuiUtils.fillBorder(inv);

        List<String> confirmLore = new ArrayList<>();
        confirmLore.add("§7Clicca per §cELIMINARE §7la home §e" + homeName);
        inv.setItem(11, GuiUtils.createItem(Material.LIME_CONCRETE, "§a✔ Conferma", confirmLore));

        List<String> cancelLore = new ArrayList<>();
        cancelLore.add("§7Clicca per annullare");
        inv.setItem(15, GuiUtils.createItem(Material.RED_CONCRETE, "§c✘ Annulla", cancelLore));

        Location loc = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
        if (loc != null) {
            List<String> homeLore = new ArrayList<>();
            homeLore.add("§7Mondo: §e" + loc.getWorld().getName());
            homeLore.add("§7X: §e" + (int) loc.getX() + " §7Y: §e" + (int) loc.getY() + " §7Z: §e" + (int) loc.getZ());
            inv.setItem(13, GuiUtils.createItem(Material.BED, "§6" + homeName, homeLore));
        }

        player.openInventory(inv);
    }
}

package cn.fusionfish.core.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class InventoryUtil {

    public static void setDenyClick(@NotNull InventoryClickEvent event, @NotNull Inventory targetInventory) {

        int size = targetInventory.getSize();

        if (event.getRawSlot() < size || event.isShiftClick()) {
            event.setCancelled(true);
        }
    }

    public static void setDenyDrag(@NotNull InventoryDragEvent event, Inventory targetInventory) {

        Inventory inventory = event.getInventory();
        if (!inventory.equals(targetInventory)) {
            return;
        }

        int size = event.getInventory().getSize();
        for (int i : event.getRawSlots()) {
            if (i < size) {
                event.setCancelled(true);
            }
        }
    }

    public static int getFreeSpace(@NotNull Inventory inventory, ItemStack itemStack) {

        int amount = 0;

        for (ItemStack invStack : inventory.getStorageContents()) {
            if (invStack.isSimilar(itemStack)) {
                amount += invStack.getMaxStackSize() - invStack.getAmount();
            }
        }

        return amount;
    }

    public static int hasItemAmount(@NotNull Inventory inventory, ItemStack itemStack) {

        int amount = 0;

        for (ItemStack invStack : inventory.getStorageContents()) {

            if (invStack.isSimilar(itemStack)) {
                amount += invStack.getAmount();
            }
        }
        return amount;
    }

}

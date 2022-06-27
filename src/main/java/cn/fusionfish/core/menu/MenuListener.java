package cn.fusionfish.core.menu;

import cn.fusionfish.core.listener.AutoRegisterListener;
import cn.fusionfish.core.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
@AutoRegisterListener
public class MenuListener implements Listener {

    @EventHandler
    public void inventoryClickEvent(@NotNull InventoryClickEvent event) {

        HumanEntity whoClicked = event.getWhoClicked();

        if (!(whoClicked instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();

        Menu menu = getMenu(inventory);
        if (menu == null) {
            return;
        }

        if (menu.isLock) {

            InventoryUtil.setDenyClick(event, inventory);
        }

        int rawSlot = event.getRawSlot();

        Button button = menu.getButton(rawSlot);

        if (button == null) {
            return;
        }

        if (button.isLock) {
            InventoryUtil.setDenyClick(event, inventory);
        } else {
            event.setCancelled(false);
        }

        if (button.clickEvent != null) {
            button.clickEvent.accept(event);
        }

        button.runCommand(player);
    }

    @EventHandler
    public void inventoryDragEvent(@NotNull InventoryDragEvent event) {

        Inventory inventory = event.getInventory();
        Menu menu = getMenu(event.getInventory());

        if (menu == null) {
            return;
        }

        if (menu.isLock) {
            InventoryUtil.setDenyDrag(event, inventory);
        }

        for (int i : event.getRawSlots()) {

            Button button = menu.getButton(i);

            if (button == null) {
                break;
            }

            if (button.isLock) {
                InventoryUtil.setDenyDrag(event, inventory);
            } else {
                event.setCancelled(false);
            }

            if (button.dragEvent != null) {
                button.dragEvent.accept(event);
            }
        }
    }

    @EventHandler
    public void inventoryOpenEvent(InventoryOpenEvent event) {

        Menu menu = getMenu(event.getInventory());

        if (menu == null || menu.openEvent == null) {
            return;
        }

        menu.openEvent.accept(event);
    }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent event) {

        Menu menu = getMenu(event.getInventory());

        if (menu == null || menu.closeEvent == null) {
            return;
        }

        menu.closeEvent.accept(event);
    }

    private Menu getMenu(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof Menu) {
            return (Menu) holder;
        }

        return null;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();
            if (getMenu(inventory) == null) {
                player.closeInventory();
            }
        }
    }


}
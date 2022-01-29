package cn.fusionfish.core.menu;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author JeremyHu
 */
public abstract class Menu implements InventoryHolder {

    public final Map<Integer, Button> buttonMap = new HashMap<>();
    public boolean isLock = true;
    public Consumer<InventoryOpenEvent> openEvent;
    public Consumer<InventoryCloseEvent> closeEvent;
    private int size = 3 * 9;
    private String title = "Menu";

    public Inventory inventory = Bukkit.createInventory(this, size, title);

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    public void setButton(@NotNull Collection<Button> buttons) {
        buttons.forEach(this::setButton);
    }

    public void setButton(@NotNull Button button) {

        for (int slot : button.slots) {
            buttonMap.put(slot, button);
            inventory.setItem(slot, button.itemStack);
        }

        if (button.action != null) {
            setDefaultButtonAction(button);
            setButtonAction(button);
        }
    }

    public void setDefaultButtonAction(@NotNull Button button) {
        if ("Close".equals(button.action)) {
            button.clickEvent = event -> event.getWhoClicked().closeInventory();
        }
    }

    public boolean isOpen(@NotNull Player player) {
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        return topInventory.equals(inventory);
    }

    public void removeButton(int slot) {
        buttonMap.remove(slot);
    }

    public boolean isButton(int slot) {
        return buttonMap.get(slot) != null;
    }

    public abstract void setButtonAction(Button button);

    public Button getButton(int slot) {
        return buttonMap.get(slot);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&',title);
        inventory = Bukkit.createInventory(this, size, this.title);
    }

    public int getSize() {
        return size;
    }

    public int getRow() {
        return size / 9;
    }

    public void setRow(int row) {
        size = row * 9;
        inventory = Bukkit.createInventory(this, size, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public static @NotNull @Unmodifiable List<Button> generateButtons(Player player) {
        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta meta = exit.getItemMeta();
        meta.setDisplayName("§c§l点击关闭菜单");
        exit.setItemMeta(meta);
        Button exitButton = new Button(exit,40);
        exitButton.clickEvent = event -> {
            player.closeInventory();
        };

        return List.of(exitButton);
    }

    public static @NotNull ItemStack createItem(Material material, String name) {
        ItemStack is = new ItemStack(material);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return is;
    }
}


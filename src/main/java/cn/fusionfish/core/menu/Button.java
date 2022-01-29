package cn.fusionfish.core.menu;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class Button {

    public ItemStack itemStack;
    public boolean isLock = true;
    public Consumer<InventoryClickEvent> clickEvent;
    public Consumer<InventoryDragEvent> dragEvent;
    public String action;
    int[] slots;
    private List<String> commands;

    public Button(ItemStack itemStack, int... slots) {
        this.itemStack = itemStack;
        this.slots = slots;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void runCommand(CommandSender commandSender) {

        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (String string : commands) {
            int separator = string.indexOf(" ");
            String type = string.substring(0, separator);
            String command = string.substring(separator + 1).replace("%player%", commandSender.getName());

            switch (type) {
                case "[CONSOLE]":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    break;

                case "[PLAYER]":

                    if (commandSender instanceof Player player) {
                        player.chat(command);
                    }
                    break;

                case "[OP]":

                    if (commandSender instanceof Player player) {

                        if (player.isOp()) {
                            player.chat(command);
                        } else {
                            player.setOp(true);
                            player.chat(command);
                            player.setOp(false);
                        }

                    }
                    break;

                default:

            }
        }

    }

}

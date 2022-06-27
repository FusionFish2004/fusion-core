package cn.fusionfish.core.actionbar;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.command.AutoRegisterCommand;
import cn.fusionfish.core.command.BukkitCompositeCommand;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author JeremyHu
 */
@AutoRegisterCommand
public class ActionBarCommand extends BukkitCompositeCommand {

    protected ActionBarCommand() {
        super("actionbar");
    }

    @SubCommand(
            command = "send.temp",
            permission = "fusioncore.actionbar.send.temp"
    )
    public void sendTemp(CommandSender sender, Player player, String text, int duration) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        TextComponent component = serializer.deserialize(text);
        ActionBarMessage message = new TempActionBarMessage(component, duration);
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        PlayerActionBar playerActionBar = manager.get(player);
        playerActionBar.add(message);
    }

    @SubCommand(
            command = "send.sustain",
            permission = "fusioncore.actionbar.send.sustain"
    )
    public void sendSustain(CommandSender sender, Player player, String text) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        TextComponent component = serializer.deserialize(text);
        ActionBarMessage message = new SustainActionBarMessage(component);
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        PlayerActionBar playerActionBar = manager.get(player);
        playerActionBar.add(message);
    }

    @SubCommand(
            command = "clear",
            permission = "fusioncore.actionbar.clear"
    )
    public void clear(CommandSender sender, Player player) {
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        PlayerActionBar playerActionBar = manager.get(player);
        playerActionBar.clear();
    }

    @SubCommand(
            command = "broadcast.temp",
            permission = "fusioncore.actionbar.broadcast.temp"
    )
    public void broadcastTemp(CommandSender sender, String text, int duration) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        TextComponent component = serializer.deserialize(text);
        ActionBarMessage message = new TempActionBarMessage(component, duration);
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        manager.broadcast(message);
    }

    @SubCommand(
            command = "broadcast.sustain",
            permission = "fusioncore.actionbar.broadcast.sustain"
    )
    public void broadcastSustain(CommandSender sender, String text) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        TextComponent component = serializer.deserialize(text);
        ActionBarMessage message = new SustainActionBarMessage(component);
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        manager.broadcast(message);
    }

    @SubCommand(
            command = "clear-all",
            permission = "fusioncore.actionbar.clear-all"
    )
    public void clearAll(CommandSender sender) {
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        manager.getActionBarMap().values().forEach(PlayerActionBar::clear);
    }

}

package cn.fusionfish.core.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class TestCompositeCommand extends BukkitCompositeCommand {

    public TestCompositeCommand() {
        super("test");
    }

    @SubCommand(command = "sub1")
    public void sub1(@NotNull CommandSender sender) {
        sender.sendMessage("invoke");
    }

    @SubCommand(command = "sub2.sub")
    public void sub2(@NotNull CommandSender sender, double d) {
        sender.sendMessage(String.valueOf(d));
    }
}

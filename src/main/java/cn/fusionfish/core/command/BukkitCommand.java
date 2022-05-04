package cn.fusionfish.core.command;

import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public abstract class BukkitCommand extends Command {

    protected BukkitCommand(@NotNull String name) {
        super(name);
    }


}

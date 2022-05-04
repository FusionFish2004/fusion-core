package cn.fusionfish.core.command;

import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public abstract class BukkitSimpleCommand extends BukkitCommand {
    protected BukkitSimpleCommand(@NotNull String name) {
        super(name);
    }

    private void execute() {

    }

    public @interface Invoke {

    }
}

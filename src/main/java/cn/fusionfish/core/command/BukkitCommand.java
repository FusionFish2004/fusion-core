package cn.fusionfish.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author JeremyHu
 */
public abstract class BukkitCommand extends Command {

    protected BukkitCommand(@NotNull String name) {
        super(name);
    }

    record SubCommandPreState(String command, String args) {
        public int getArgLength() {
            return args.split("\\.").length;
        }

        public int getCommandLength() {
            return command.split("\\.").length;
        }

        public int getTotalLength() {
            return getArgLength() + getCommandLength();
        }

        @Contract(pure = true)
        public String @NotNull [] getTypes() {
            return args.split("\\.");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SubCommandPreState that = (SubCommandPreState) o;
            return command.equals(that.command) && args.equals(that.args);
        }

        @Override
        public int hashCode() {
            return Objects.hash(command, args);
        }

        @Contract(pure = true)
        @Override
        public @NotNull String toString() {
            return "SubCommandPreState{" +
                    "command='" + command + '\'' +
                    ", args='" + args + '\'' +
                    '}';
        }
    }

    public static String getDefaultUsage(@NotNull BukkitCommand command, @NotNull Method method) {
        StringJoiner defaultUsage = new StringJoiner(" ", "/", "");
        defaultUsage.add(command.getName());
        Arrays.stream(method.getParameterTypes())
                .filter(clazz -> !clazz.equals(CommandSender.class))
                .map(clazz -> "<" + clazz.getSimpleName() + ">")
                .forEach(defaultUsage::add);
        return defaultUsage.toString();
    }
}

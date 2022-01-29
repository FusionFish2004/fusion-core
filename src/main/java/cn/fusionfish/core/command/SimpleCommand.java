package cn.fusionfish.core.command;

import cn.fusionfish.core.annotations.FusionCommand;
import cn.fusionfish.core.plugin.FusionPlugin;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static cn.fusionfish.core.utils.ConsoleUtil.warn;

/**
 * @author JeremyHu
 */

public abstract class SimpleCommand extends Command {

    public final FusionPlugin plugin = FusionPlugin.getInstance();

    public SimpleCommand getParent() {
        return parent;
    }

    private final FusionCommand annotation = this.getClass().getAnnotation(FusionCommand.class);

    protected final SimpleCommand parent;

    private final Map<String, SimpleCommand> subCommands = Maps.newLinkedHashMap();
    private final List<String> tabComplete = Lists.newArrayList();

    protected CommandSender sender;
    protected String[] args;

    private boolean isAdminCommand = false;
    private boolean isPlayerOnly = false;

    public SimpleCommand() {
        super("","","", Lists.newArrayList());
        setLabel(annotation.label());
        setDescription(annotation.description());
        setUsage(annotation.usage());
        setPermission(annotation.perm());
        setAliases(Lists.newArrayList(annotation.aliases()));

        if (annotation.adminCommand()) {
            setAdminCommand();
        }

        if (annotation.playerCommand()) {
            setPlayerOnly();
        }

        //获取父命令
        if (!"".equals(annotation.parent())) {
            this.parent = null;
            return;
        }

        Set<SimpleCommand> commands = plugin.getCommands();
        this.parent = commands.stream()
                .filter(simpleCommand -> simpleCommand.getClass().getSimpleName().equalsIgnoreCase(annotation.parent()))
                .findFirst()
                .orElse(null);

        plugin.getCommandManager().registerCommand(this);
    }

    public void setAdminCommand() {
        isAdminCommand = true;
    }

    public void setPlayerOnly() {
        isPlayerOnly = true;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        this.sender = sender;
        this.args = args;

        SimpleCommand simpleCommand = getCommandFromArgs(args);

        simpleCommand.sender = sender;
        simpleCommand.args = args;

        if (simpleCommand.isPlayerOnly && simpleCommand.sender instanceof ConsoleCommandSender) {
            warn("该命令只能由玩家执行");
            return true;
        }

        try {
            simpleCommand.onCommand();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return true;
    }

    public Optional<SimpleCommand> getSubCommand(String label) {
        if (subCommands.containsKey(label)) {
            return Optional.ofNullable(subCommands.get(label));
        }
        return Optional.empty();
    }

    /**
     * @return Map of sub commands for this command
     */
    public Map<String, SimpleCommand> getSubCommands() {
        return subCommands;
    }

    /**
     * 深度获取所有子命令
     * @return 子命令
     */
    public List<SimpleCommand> getAllSubCommands() {

        List<SimpleCommand> buffer = Lists.newArrayList();

        Stack<SimpleCommand> commandStack = new Stack<>();
        SimpleCommand command = this;
        commandStack.add(command);

        while (!commandStack.isEmpty()) {
            command = commandStack.pop();
            buffer.add(command);
            List<SimpleCommand> subCommands = new ArrayList<>(command.getSubCommands().values());
            if (!subCommands.isEmpty()) {
                for (SimpleCommand e : subCommands) {
                    commandStack.push(e);
                }
            }
        }

        return buffer;
    }

    protected boolean hasSubCommands() {
        return !subCommands.isEmpty();
    }

    protected void setTabComplete(String[] options) {
        tabComplete.addAll(Arrays.asList(options));
    }

    public Player getPlayer() {
        return (Player) sender;
    }

    private SimpleCommand getCommandFromArgs(String @NotNull [] args) {

        SimpleCommand simpleCommand = this;

        // Run through any arguments
        for (String arg : args) {
            // 没有子命令就结束循环
            if (!simpleCommand.hasSubCommands()) {
                return simpleCommand;
            }

            Optional<SimpleCommand> subCommand = simpleCommand.getSubCommand(arg);
            if (subCommand.isEmpty()) {
                return simpleCommand;
            }

            boolean isAdminCommand = subCommand.get().isAdminCommand;
            boolean isOp = sender.isOp();

            if (isAdminCommand && !isOp) {
                return simpleCommand;
            }

            // Step down one
            simpleCommand = subCommand.orElse(simpleCommand);
        }

        return simpleCommand;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

        this.sender = sender;
        SimpleCommand command = getCommandFromArgs(args);

        List<String> options = new ArrayList<>(command.subCommands.keySet());
        options.addAll(command.tabComplete);
        options.removeIf(s -> !s.startsWith(args[0].toLowerCase()));

        return options;
    }

    public abstract void onCommand();

    public void sendMsg(String msg) {
        sender.sendMessage("§c" + msg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleCommand that = (SimpleCommand) o;
        return isAdminCommand == that.isAdminCommand && isPlayerOnly == that.isPlayerOnly && getParent().equals(that.getParent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), isAdminCommand, isPlayerOnly);
    }
}

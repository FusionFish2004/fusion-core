package cn.fusionfish.core.command;

import cn.fusionfish.core.manager.Manager;
import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.utils.parser.ParamParser;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author JeremyHu
 */
@Log4j2
public final class CommandManager implements Manager {

    private final Map<String, Command> commands = Maps.newHashMap();
    private final Plugin plugin;
    private final CommandMap commandMap;

    public CommandManager() {
        this.plugin = FusionPlugin.getInstance();
        commandMap = getCommandMap();
    }

    public void registerCommand(BukkitCommand command) {
        commandMap.register(plugin.getName(), command);
        commands.put(command.getLabel(), command);
    }

    public void registerParser(ParamParser<?> paramParser) {

    }


    public void unregisterCommands() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) commandMap.getClass()
                    .getMethod("getKnownCommands")
                    .invoke(commandMap);

            knownCommands.values().removeIf(commands.values()::contains);
            commands.values().forEach(c -> c.unregister(commandMap));
            commands.clear();

        } catch(Exception e){

            log.error("卸载命令异常, 请重启服务器!");

        }

    }

    public void updateCommands() {
        CraftServer server = (CraftServer) Bukkit.getServer();
        server.syncCommands();
    }

    private static @Nullable CommandMap getCommandMap() {

        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

        } catch (NoSuchFieldException | IllegalAccessException e) {

            e.printStackTrace();

        }

        return null;
    }

}


package cn.fusionfish.core.plugin;

import cn.fusionfish.core.annotations.Command;
import cn.fusionfish.core.command.CommandManager;
import cn.fusionfish.core.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.fusionfish.core.utils.ConsoleUtil.error;
import static cn.fusionfish.core.utils.ConsoleUtil.info;

public abstract class FusionPlugin extends JavaPlugin {

    private static FusionPlugin instance;
    private Set<Listener> listeners;
    private Set<SimpleCommand> commands;
    private CommandManager commandManager;
    private Reflections reflections;
    public final File CONFIG_FILE = new File(getDataFolder(), "config.yml");

    @Override
    public final void onEnable() {
        instance = this;
        commandManager = new CommandManager();

        if (!isCore()) {
            info("插件" + getName() + "成功启动.");
            info("Powered by FusionCore.");
        }

        this.reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(getPluginFile())
        );

        loadConfig();

        registerListeners();
        registerCommands();

        enable();


    }

    @Override
    public final void onDisable() {
        disable();
    }

    public static FusionPlugin getInstance() {
        return instance;
    }

    private void registerCommands() {

        if (isCore()) return;

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Command.class);
        //获取所有类
        this.commands = classes.stream()
                .map(clazz -> {
                    try {
                        return (SimpleCommand) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toSet());
        commands.forEach(command -> commandManager.registerCommand(command));
        //注册命令
        int size = classes.size();
        if (size > 0) {
            info("成功注册" + size + "个命令.");
        }
    }

    private void registerListeners() {

        if (isCore()) return;

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(cn.fusionfish.core.annotations.Listener.class);
        //获取所有类
        this.listeners = classes.stream()
                .map(clazz -> {
                    try {
                        return (Listener) clazz.getDeclaredConstructor().newInstance();
                        //实例化对象
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toSet());
        listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener,this));
        //注册监听器
        int size = classes.size();
        if (size > 0) {
            info("成功注册" + size + "个监听器.");
        }
    }

    public final Set<Listener> getListeners() {
        return listeners;
    }

    public final Set<SimpleCommand> getCommands() {
        return commands;
    }

    private void loadConfig() {
        InputStream in = this.getResource("config.yml");

        if (in == null) {
            return;
        }
        //插件中不存在默认配置

        if (!getDataFolder().exists()) {
            info("正在创建插件数据文件夹...");
            boolean result = getDataFolder().mkdir();
            if (!result) {
                error("创建插件数据文件夹失败！");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        if (!CONFIG_FILE.exists()) {
            info("正在创建默认插件配置文件...");
            saveDefaultConfig();
        }
    }

    public final CommandManager getCommandManager() {
        return commandManager;
    }

    protected abstract void enable();
    protected abstract void disable();

    public URL getPluginFile() {
        URL url = this.getClassLoader().getResource("plugin.yml");
        assert url != null;
        String newStr = url.getPath().replace("plugin,yml", "");
        try {
            return new URL(newStr);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean isCore() {
        return this.getName().equals("FusionCore");
    }

}

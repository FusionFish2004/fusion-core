package cn.fusionfish.core.plugin;

import cn.fusionfish.core.annotations.FusionCommand;
import cn.fusionfish.core.annotations.FusionListener;
import cn.fusionfish.core.command.CommandManager;
import cn.fusionfish.core.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.fusionfish.core.utils.ConsoleUtil.error;
import static cn.fusionfish.core.utils.ConsoleUtil.info;

/**
 * @author JeremyHu
 */
public abstract class FusionPlugin extends JavaPlugin {

    private static FusionPlugin instance;
    private Set<Listener> listeners;
    private Set<SimpleCommand> commands;
    private CommandManager commandManager;
    private Reflections reflections;
    public final File CONFIG_FILE = new File(getDataFolder(), "config.yml");
    private Connection connection;
    private final BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            tick();
        }
    };

    @Override
    public final void onEnable() {
        instance = this;
        commandManager = new CommandManager();

        if (!isCore()) {
            info("插件" + getName() + "成功启动.");
            info("Powered by FusionCore.");
        }

        String msg = """
                
                ================================================================================
                ███████╗██╗   ██╗███████╗██╗ ██████╗ ███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
                ██╔════╝██║   ██║██╔════╝██║██╔═══██╗████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
                █████╗  ██║   ██║███████╗██║██║   ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗\040
                ██╔══╝  ██║   ██║╚════██║██║██║   ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝\040
                ██║     ╚██████╔╝███████║██║╚██████╔╝██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
                ╚═╝      ╚═════╝ ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
                ================================================================================\040
                前置插件FusionCore已加载成功！
                """;
        StringBuilder sb = new StringBuilder(msg);
        Set<@NotNull Plugin> supportedPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getDescription().getDepend().contains("FusionCore"))
                .collect(Collectors.toSet());
        if (!supportedPlugins.isEmpty()) {
            sb.append("\n下列插件（使用FusionCore前置）已经准备好加入服务器：");
            supportedPlugins.stream()
                    .map(Plugin::getName)
                    .forEach(name -> sb.append("\n - ").append(name));
        }
        info(sb.toString());

        this.reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(getPluginFile())
        );

        //初始化插件文件夹
        initPluginFolder();

        //加载配置
        loadConfig();

        //初始化数据库
        initDatabase();

        //注册监听器和命令
        registerListeners();
        registerCommands();

        //触发子类enable方法
        enable();

        tick();
    }

    @Override
    public final void onDisable() {
        info("正在注销指令...");
        commandManager.unregisterCommands();
        disable();
    }

    private void initPluginFolder() {
        if (!getDataFolder().exists()) {
            info("正在创建插件数据文件夹...");
            boolean result = getDataFolder().mkdir();
            if (!result) {
                error("创建插件数据文件夹失败！");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }

    private void initDatabase() {
        info("正在初始化数据库...");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/FusionCore/shared_data.db");
            info("数据库初始化成功！");
        } catch (Exception e) {
            e.printStackTrace();
            error("初始化数据库失败！");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static FusionPlugin getInstance() {
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public Reflections getReflections() {
        return reflections;
    }

    private void registerCommands() {

        if (isCore()) {
            return;
        }

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FusionCommand.class);
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

        if (isCore()) {
            return;
        }

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FusionListener.class);
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

    private @Nullable URL getPluginFile() {
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
        return "FusionCore".equals(this.getName());
    }

    private void tick() {

    }
}

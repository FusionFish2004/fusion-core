package cn.fusionfish.core.plugin;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.actionbar.ActionBarManager;
import cn.fusionfish.core.annotations.FusionListener;
import cn.fusionfish.core.command.BukkitCommand;
import cn.fusionfish.core.command.CommandManager;
import cn.fusionfish.core.web.http.ServerController;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
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
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.fusionfish.core.FusionCore.getCore;
import static cn.fusionfish.core.utils.ConsoleUtil.error;
import static cn.fusionfish.core.utils.ConsoleUtil.info;

/**
 * 代表一个插件
 * 所有使用本前置的插件均应当继承此父类
 * @author JeremyHu
 */
public abstract class FusionPlugin extends JavaPlugin {

    private static FusionPlugin instance;
    private Set<Listener> listeners;
    private Set<BukkitCommand> commands;
    private CommandManager commandManager;
    private Reflections reflections;
    public final File CONFIG_FILE = new File(getDataFolder(), "config.yml");
    public final File SERVER_ROOT = getDataFolder().getParentFile().getParentFile();

    @Override
    public final void onEnable() {

        instance = this;
        commandManager = new CommandManager();

        if (!isCore()) {
            info("插件" + getName() + " v" + getDescription().getVersion() + " 成功启动.");
            info("Powered by FusionCore.");
        }

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

        //确认加载完成
        getCore().loadComplete(this);

    }

    public static ActionBarManager getActionBarManager() {
        return FusionCore.getActionBarManager();
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
            Connection connection = getConnection();
            connection.close();
            info("数据库初始化成功！");
        } catch (Exception e) {
            e.printStackTrace();
            error("初始化数据库失败！");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:plugins/FusionCore/shared_data.db");
    }

    public static FusionPlugin getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T extends FusionPlugin> @Nullable T getInstance(@NotNull Class<T> clazz) {
        if (!clazz.getSuperclass().equals(FusionPlugin.class)) {
            return null;
        }
        return (T)instance;
    }

    public Reflections getReflections() {
        return reflections;
    }

    private void registerCommands() {

        //新指令的支持
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(cn.fusionfish.core.annotations.BukkitCommand.class);
        this.commands = classes.stream()
                .map(clazz -> {
                    try {
                        return (BukkitCommand) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        commands.forEach(getCommandManager()::registerCommand);

        if (isCore()) {
            return;
        }

        int size = classes.size();
        if (size > 0) {
            info("成功注册" + size + "个命令.");
        }
    }

    private void registerListeners() {

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

        if (isCore()) {
            return;
        }

        int size = classes.size();
        if (size > 0) {

            info("成功注册" + size + "个监听器.");
        }
    }

    @SuppressWarnings("unused")
    public final Set<Listener> getListeners() {
        return listeners;
    }

    public final Set<BukkitCommand> getCommands() {
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

    @SuppressWarnings("unused")
    public final CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * 插件启动时触发本方法
     */
    protected abstract void enable();

    /**
     * 插件关闭时触发本方法
     */
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

    public boolean isCore() {
        return "FusionCore".equals(this.getName());
    }

    public ServerController getServerController() {
        return getCore().getServerController();
    }

}

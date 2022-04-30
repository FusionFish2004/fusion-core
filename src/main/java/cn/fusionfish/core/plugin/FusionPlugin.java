package cn.fusionfish.core.plugin;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.annotations.FusionCommand;
import cn.fusionfish.core.annotations.FusionListener;
import cn.fusionfish.core.command.CommandManager;
import cn.fusionfish.core.command.SimpleCommand;
import cn.fusionfish.core.utils.FileUtil;
import cn.fusionfish.core.utils.StringUtil;
import cn.fusionfish.core.web.http.ServerController;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    private Set<SimpleCommand> commands;
    private CommandManager commandManager;
    private Reflections reflections;
    private Set<@NotNull Plugin> supportedPlugins;
    public final File CONFIG_FILE = new File(getDataFolder(), "config.yml");
    public final File SERVER_ROOT = getDataFolder().getParentFile().getParentFile();
    private FusionCore core;
    private ServerController serverController;

    @Override
    public final void onEnable() {

        instance = this;
        commandManager = new CommandManager();
        core = getCore();

        if (!isCore()) {
            info("插件" + getName() + "成功启动.");
            info("Powered by FusionCore.");
        } else {
            String msg = """
                ================================================================================
                ███████╗██╗   ██╗███████╗██╗ ██████╗ ███╗   ██╗ ██████╗ ██████╗ ██████╗ ███████╗
                ██╔════╝██║   ██║██╔════╝██║██╔═══██╗████╗  ██║██╔════╝██╔═══██╗██╔══██╗██╔════╝
                █████╗  ██║   ██║███████╗██║██║   ██║██╔██╗ ██║██║     ██║   ██║██████╔╝█████╗\040
                ██╔══╝  ██║   ██║╚════██║██║██║   ██║██║╚██╗██║██║     ██║   ██║██╔══██╗██╔══╝\040
                ██║     ╚██████╔╝███████║██║╚██████╔╝██║ ╚████║╚██████╗╚██████╔╝██║  ██║███████╗
                ╚═╝      ╚═════╝ ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
                ================================================================================\040
                前置插件FusionCore已加载成功！""";
            info(StringUtil.breakLines(msg));

            supportedPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .filter(plugin -> plugin.getDescription().getDepend().contains("FusionCore"))
                    .collect(Collectors.toSet());
            if (!supportedPlugins.isEmpty()) {
                info("下列插件（使用FusionCore前置）已经准备好加入服务器：");
                supportedPlugins.forEach(plugin -> info(" - " + plugin.getName() + " - v" + plugin.getDescription().getVersion()));
            }
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

        createHTTPServer();

        if (!isCore()) {
            return;
        }

        checkLoadState();
    }

    @Override
    public final void onDisable() {
        info("正在注销指令...");
        commandManager.unregisterCommands();

        if (serverController != null) {
            info("正在关闭HTTP服务器...");
            serverController.stop();
        }

        disable();

        if (isCore()) {
            info("前置插件关闭，正在关闭所有使用前置的插件...");
            PluginManager pluginManager = Bukkit.getPluginManager();
            supportedPlugins.stream()
                    .filter(pluginManager::isPluginEnabled)
                    .forEach(pluginManager::disablePlugin);
        }
    }

    private void loadSupportedPlugins() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (!isCore()) {
            return;
        }

        File pluginsFolder = getDataFolder().getParentFile();

        List<File> files = FileUtil.getFiles(pluginsFolder);

        if (files == null) {
            return;
        }

        Set<File> disabledSupportedPluginFiles = files.stream()
                .filter(file -> FileUtil.getExtension(file).equalsIgnoreCase(FileUtil.EXTENSION_JAR))
                .filter(File::isFile)
                .filter(file -> {
                    try {
                        URL fileUrl = file.toURI().toURL();
                        URL url = new URL("jar:" + fileUrl + "!/plugin.yml");
                        InputStream inputStream = url.openStream();
                        PluginDescriptionFile description = new PluginDescriptionFile(inputStream);
                        return (description.getDepend()
                                .stream()
                                .anyMatch("FusionCore"::equals) &&
                                !pluginManager.isPluginEnabled(description.getName()));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toSet());

        if (disabledSupportedPluginFiles.isEmpty()) {
            return;
        }

        info("检测到有" + disabledSupportedPluginFiles.size() + "个使用FusionCore前置的插件未启用！正在启动...");
        disabledSupportedPluginFiles.stream()
                .map(file -> {
                    try {
                        return pluginManager.loadPlugin(file);
                    } catch (InvalidPluginException | InvalidDescriptionException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(pluginManager::enablePlugin);


    }

    private void checkLoadState() {
        File logFolder = new File(SERVER_ROOT, "logs");
        File logFile = new File(logFolder, "latest.log");
        CompletableFuture<Boolean> cf = CompletableFuture.supplyAsync(() -> {
            FileInputStream fileInputStream = null;
            BufferedReader reader = null;
            try {
                fileInputStream = new FileInputStream(logFile);
                reader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                boolean result = false;
                while ((line = reader.readLine()) != null) {
                    result = line.contains("[Server thread/INFO]: Done");
                    if (result) {
                        break;
                    }
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cf.thenAccept(bool -> {
            if (bool == null) {
                return;
            }

            if (bool) {
                loadSupportedPlugins();
            }
        });
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

    public Reflections getReflections() {
        return reflections;
    }

    private void registerCommands() {

        //获取所有类
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(FusionCommand.class);

        this.commands = classes.stream()
                .map(clazz -> {
                    try {
                        return (SimpleCommand) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        commands.forEach(SimpleCommand::init);
        //仅保留父命令
        commands.removeIf(command -> !command.isParentCommand());

        //注册命令
        commands.forEach(command -> commandManager.registerCommand(command));

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

    @SuppressWarnings("unused")
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

    public boolean isCore() {
        return "FusionCore".equals(this.getName());
    }

    private void createHTTPServer() {
        if (!isCore()) {
            return;
        }

        try {
            int port = getConfig().getInt("web.http-port", 11451);
            info("启动HTTP服务器（端口" + port + "）");
            serverController = new ServerController(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServerController getServerController() {
        if (serverController != null) {
            return serverController;
        }

        return core.getServerController();
    }

    public static FusionCore getCore() {
        return (FusionCore) Bukkit.getPluginManager().getPlugin("FusionCore");
    }


}

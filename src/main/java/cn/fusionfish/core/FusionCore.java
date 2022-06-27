package cn.fusionfish.core;

import cn.fusionfish.core.exception.HttpServerNotDeployingException;
import cn.fusionfish.core.manager.Manager;
import cn.fusionfish.core.manager.ManagerManager;
import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.utils.FileUtil;
import cn.fusionfish.core.utils.StringUtil;
import cn.fusionfish.core.utils.parser.ParserFactory;
import cn.fusionfish.core.web.http.ServerController;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.plugin.*;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static cn.fusionfish.core.utils.ConsoleUtil.info;
import static cn.fusionfish.core.utils.ConsoleUtil.infoCore;

/**
 * @author JeremyHu
 */
public final class FusionCore extends FusionPlugin {

    private Set<@NotNull FusionPlugin> supportedPlugins;
    private ServerController serverController;
    private static FusionCore core;
    private final Map<FusionPlugin, Boolean> pluginLoadConfirmBuffer = Maps.newHashMap();

    private static ParserFactory parserFactory;

    private static ManagerManager managerManager;

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    protected void enable() {

        core = this;

        parserFactory = new ParserFactory();

        managerManager = new ManagerManager();
        managerManager.registerManagers(this);

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

        String bukkitVersion = Bukkit.getServer().getBukkitVersion();
        info("在" + bukkitVersion + "平台运行中.");

        supportedPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getDescription().getDepend().contains("FusionCore"))
                .map(plugin -> (FusionPlugin) plugin)
                .collect(Collectors.toSet());

        supportedPlugins.forEach(plugin -> pluginLoadConfirmBuffer.put(plugin,false));

        if (!supportedPlugins.isEmpty()) {
            info("下列插件（使用FusionCore前置）准备加入服务器：");
            supportedPlugins.forEach(plugin -> info(" - " + plugin.getName() + " - v" + plugin.getDescription().getVersion()));
        }

        //注册所有http服务
        initHttpService();

        registerManagers();

        checkLoadState();

    }

    public static ParserFactory getParserFactory() {
        return parserFactory;
    }

    private void registerManagers() {
        supportedPlugins.forEach(managerManager::registerManagers);
    }

    public static <T extends Manager> T getManager(Class<T> managerClass) {
        return managerManager.getManager(managerClass);
    }

    @Override
    public ServerController getServerController() {
        return serverController;
    }

    private void checkLoadState() {
        Runnable runnable = () -> {
            while (true) {
                if (pluginLoadConfirmBuffer.isEmpty()) {
                    break;
                }

                if (pluginLoadConfirmBuffer.values().stream().allMatch(Boolean::booleanValue)) {
                    //所有插件加载完成
                    break;
                }
            }
            infoCore("加载成功！");
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTask(core, this::afterLoad);
        };
        service.execute(runnable);
    }

    public void loadComplete(FusionPlugin plugin) {
        if (pluginLoadConfirmBuffer.containsKey(plugin)) {
            pluginLoadConfirmBuffer.put(plugin, true);
        }
    }

    private void afterLoad() {
        //注册所有HTTP监听器
        supportedPlugins.forEach(serverController::loadHandlers);
    }

    public static FusionCore getCore() {
        return core;
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
                                .anyMatch("FusionCore"::equals) && !pluginManager.isPluginEnabled(description.getName()));
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

    private void initHttpService() {

        boolean httpService = getConfig().getBoolean("web.http-service", false);
        if (!httpService) {
            return;
        }

        try {
            int port = getConfig().getInt("web.http-port", 8088);
            info("启动HTTP服务器（端口" + port + "）");
            serverController = new ServerController(port);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpServerNotDeployingException e) {
            serverController = null;
        }
    }

    @Override
    protected void disable() {

        info("前置插件关闭，正在关闭所有使用前置的插件...");
        PluginManager pluginManager = Bukkit.getPluginManager();
        supportedPlugins.stream()
                .filter(pluginManager::isPluginEnabled)
                .forEach(pluginManager::disablePlugin);

        if (serverController != null) {
            info("正在关闭HTTP服务器...");
            serverController.stop();
        }
    }
}

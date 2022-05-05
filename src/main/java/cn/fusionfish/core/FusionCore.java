package cn.fusionfish.core;

import cn.fusionfish.core.exception.HttpServerNotDeployingException;
import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.utils.ConsoleUtil;
import cn.fusionfish.core.utils.FileUtil;
import cn.fusionfish.core.utils.StringUtil;
import cn.fusionfish.core.web.http.ServerController;
import org.bukkit.Bukkit;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static cn.fusionfish.core.utils.ConsoleUtil.info;
import static cn.fusionfish.core.utils.ConsoleUtil.infoCore;

/**
 * @author JeremyHu
 */
public final class FusionCore extends FusionPlugin {

    private Set<@NotNull Plugin> supportedPlugins;
    private ServerController serverController;
    private static FusionCore core;

    @Override
    protected void enable() {

        core = this;

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
            info("下列插件（使用FusionCore前置）准备加入服务器：");
            supportedPlugins.forEach(plugin -> info(" - " + plugin.getName() + " - v" + plugin.getDescription().getVersion()));
        }

        initHttpService();

        checkLoadState();
    }

    @Override
    public ServerController getServerController() {
        return serverController;
    }

    private void checkLoadState() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            while (true) {
                long unable = supportedPlugins.stream()
                        .filter(plugin -> !plugin.isEnabled())
                        .count();
                if (unable == 0) {
                    break;
                }
            }
            return null;
        });
        future.thenAccept(v -> {
            infoCore("加载成功！");
            afterLoad();
        });
    }

    private void afterLoad() {
        //注册所有HTTP监听器
        supportedPlugins.stream()
                .map(plugin -> (FusionPlugin) plugin)
                .forEach(serverController::loadHandlers);
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
            int port = getConfig().getInt("web.http-port", 11451);
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

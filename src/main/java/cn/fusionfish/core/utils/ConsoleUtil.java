package cn.fusionfish.core.utils;

import cn.fusionfish.core.plugin.FusionPlugin;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author JeremyHu
 */
public class ConsoleUtil {

    public static void info(String str) {
        FusionPlugin plugin = FusionPlugin.getInstance();
        Logger logger = plugin.getLogger();
        logger.info(ChatColor.GREEN + str);
    }

    public static void info(@NotNull List<String> str) {
        str.forEach(ConsoleUtil::info);
    }

    public static void warn(String str) {
        FusionPlugin plugin = FusionPlugin.getInstance();
        Logger logger = plugin.getLogger();
        logger.warning(str);
    }

    public static void warn(@NotNull List<String> str) {
        str.forEach(ConsoleUtil::warn);
    }

    public static void error(String str) {
        FusionPlugin plugin = FusionPlugin.getInstance();
        Logger logger = plugin.getLogger();
        logger.severe(str);
    }

    public static void error(@NotNull List<String> str) {
        str.forEach(ConsoleUtil::error);
    }
}

package cn.fusionfish.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author JeremyHu
 */
public class StringUtil {
    public static boolean fuzzyMatch(@NotNull String keyword, @NotNull String origin) {
        return origin.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }
}

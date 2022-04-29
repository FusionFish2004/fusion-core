package cn.fusionfish.core.utils;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * @author JeremyHu
 */
public class StringUtil {
    public static boolean fuzzyMatch(@NotNull String keyword, @NotNull String origin) {
        return origin.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    @Contract("_ -> new")
    public static @NotNull List<String> breakLines(@NotNull String string) {
        return Lists.newArrayList(string.split("\\r?\\n"));
    }
}

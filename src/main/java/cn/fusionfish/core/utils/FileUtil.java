package cn.fusionfish.core.utils;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileUtil {
    public FileUtil() {
    }

    @Nullable
    public static String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();

            String str;
            while((str = reader.readLine()) != null) {
                stringBuilder.append(str).append("\n");
            }

            reader.close();
            return stringBuilder.toString();
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static void saveJson(File file, String json) {
        try {
            if (!file.exists()) {
                boolean result = file.createNewFile();
                if (!result) {
                    throw new RuntimeException();
                }
            }

            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(json);
            write.flush();
            write.close();
        } catch (Exception ignored) {

        }

    }

    @Nullable
    public static List<File> getFiles(@NotNull File dir) {
        return dir.isDirectory() && dir.exists() ? new ArrayList<>(Arrays.asList(Objects.requireNonNull(dir.listFiles()))) : null;
    }
}

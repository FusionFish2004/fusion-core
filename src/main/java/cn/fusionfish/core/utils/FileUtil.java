package cn.fusionfish.core.utils;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author JeremyHu
 */
@SuppressWarnings("unused")
public class FileUtil {

    public static final String EXTENSION_JAR = ".jar";

    @Nullable
    public static String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();

            String str;
            while ((str = reader.readLine()) != null) {
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

    public static @NotNull String getExtension(@NotNull File file) {
        String fileName = file.getAbsolutePath();
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @SuppressWarnings("all")
    public static void copy(String path, String copyPath) throws IOException {
        File filePath = new File(path);
        DataInputStream read;
        DataOutputStream write;
        if (filePath.isDirectory()) {
            File[] list = filePath.listFiles();
            assert list != null;
            for (File file : list) {
                String newPath = path + File.separator + file.getName();
                String newCopyPath = copyPath + File.separator + file.getName();
                File newFile = new File(copyPath);
                if (!newFile.exists()) {
                    newFile.mkdir();
                }
                copy(newPath, newCopyPath);
            }
        } else if (filePath.isFile()) {
            read = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(path)));
            write = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(copyPath)));
            byte[] buf = new byte[1024 * 512];
            while (read.read(buf) != -1) {
                write.write(buf);
            }
            read.close();
            write.close();
        } else {
            System.out.println("请输入正确的文件名或路径名");
        }
    }

    @SuppressWarnings("all")
    public static boolean deleteFolder(@NotNull File file) {
        try {
            Stream<Path> files = Files.walk(file.toPath());

            boolean var2;
            try {
                files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                var2 = true;
            } catch (Throwable var5) {
                if (files != null) {
                    try {
                        files.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }
                }

                throw var5;
            }

            files.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("all")
    public static boolean deleteFolderContents(@NotNull File file) {
        try {
            Stream<Path> files = Files.walk(file.toPath());

            boolean var2;
            try {
                files.sorted(Comparator.reverseOrder()).map(Path::toFile).filter((f) -> !f.equals(file)).forEach(File::delete);
                var2 = true;
            } catch (Throwable var5) {
                if (files != null) {
                    try {
                        files.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }
                }

                throw var5;
            }

            files.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFolder(File source, File target) {
        return copyFolder(source, target, null);
    }


    public static boolean copyFolder(@NotNull File source, @NotNull File target, List<String> excludeFiles) {
        Path sourceDir = source.toPath();
        Path targetDir = target.toPath();

        try {
            Files.walkFileTree(sourceDir, new CopyDirFileVisitor(sourceDir, targetDir, excludeFiles));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class CopyDirFileVisitor extends SimpleFileVisitor<Path> {
        private final Path sourceDir;
        private final Path targetDir;
        private final List<String> excludeFiles;

        private CopyDirFileVisitor(Path sourceDir, Path targetDir, List<String> excludeFiles) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
            this.excludeFiles = excludeFiles;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path newDir = this.targetDir.resolve(this.sourceDir.relativize(dir));
            if (!Files.isDirectory(newDir)) {
                Files.createDirectory(newDir);
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (this.excludeFiles == null || !this.excludeFiles.contains(file.getFileName().toString())) {
                Path targetFile = this.targetDir.resolve(this.sourceDir.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}

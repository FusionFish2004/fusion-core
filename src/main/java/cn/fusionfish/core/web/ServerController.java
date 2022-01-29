package cn.fusionfish.core.web;

import cn.fusionfish.core.annotations.FusionHandler;
import cn.fusionfish.core.plugin.FusionPlugin;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author JeremyHu
 */
public final class ServerController {

    private static final ThreadFactory FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("fusion-core-pool-%d")
            .build();
    private final HttpServer server;
    private final Reflections reflections = FusionPlugin.getInstance().getReflections();
    private Set<Handler> handlers;
    private final int port;

    public ServerController(int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(this.port),0);
        //反射加载handler
        loadHandlers();
        //创建地址
        createContexts();
        server.setExecutor(newCachedThreadPool());
        server.start();
    }

    private void loadHandlers() {
        handlers = reflections.getTypesAnnotatedWith(FusionHandler.class).stream()
                .filter(clazz -> "cn.fusionfish.core.web.Handler".equals(clazz.getSuperclass().getName()))
                .map(clazz -> {
                    try {
                        return (Handler) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(handler -> !"".equals(handler.getPath()))
                .collect(Collectors.toSet());
    }

    private void createContexts() {
        handlers.forEach(handler -> {
            String path = handler.getPath();
            server.createContext(path, handler);
        });
    }

    public HttpServer getServer() {
        return server;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public int getPort() {
        return port;
    }

    @Contract(" -> new")
    public static @NotNull ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), FACTORY);
    }
}

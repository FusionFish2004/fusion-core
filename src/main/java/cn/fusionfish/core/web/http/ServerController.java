package cn.fusionfish.core.web.http;

import cn.fusionfish.core.annotations.FusionHandler;
import cn.fusionfish.core.plugin.FusionPlugin;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author JeremyHu
 */
@SuppressWarnings("unused")
public final class ServerController {

    private final HttpServer server;
    private final Reflections reflections = FusionPlugin.getInstance().getReflections();
    private Set<Handler> handlers;
    private final int port;

    @SuppressWarnings("all")
    public ServerController(int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(this.port),0);
        //反射加载handler
        loadHandlers();
        //创建地址
        createContexts();
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    private void loadHandlers() {
        handlers = reflections.getTypesAnnotatedWith(FusionHandler.class).stream()
                .filter(clazz -> "cn.fusionfish.core.web.http.Handler".equals(clazz.getSuperclass().getName()))
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

    private void createContext(@NotNull Handler handler) {
        String path = handler.getPath();
        server.createContext(path, handler);
        handlers.add(handler);
    }

    public Set<Handler> getHandlers() {
        return handlers;
    }

    private void createContexts() {
        handlers.forEach(this::createContext);
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

}

package cn.fusionfish.core.web.http;

import cn.fusionfish.core.FusionCore;
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
    private Set<Handler> handlers;
    private final int port;

    @SuppressWarnings("all")
    public ServerController(int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(this.port),0);
        //反射加载handler
        loadHandlers(FusionCore.getCore());
        //创建地址
        createContexts();
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void loadHandlers(@NotNull FusionPlugin plugin) {
        Reflections reflections = plugin.getReflections();
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

    public void createContext(@NotNull Handler handler) {
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

    public void stop() {
        server.stop(0);
    }


    public int getPort() {
        return port;
    }

}

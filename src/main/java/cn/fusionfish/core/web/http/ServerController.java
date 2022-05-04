package cn.fusionfish.core.web.http;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.annotations.FusionHandler;
import cn.fusionfish.core.exception.HttpServerNotDeployingException;
import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.utils.ConsoleUtil;
import com.google.common.collect.Sets;
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
    private final Set<Handler> handlers = Sets.newHashSet();
    private final int port;

    @SuppressWarnings("all")
    public ServerController(int port) throws IOException, HttpServerNotDeployingException {

        this.port = port;

        if (port == 0) {
            throw new HttpServerNotDeployingException();
        }

        server = HttpServer.create(new InetSocketAddress(this.port),0);

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void loadHandlers(@NotNull FusionPlugin plugin) {
        Reflections reflections = plugin.getReflections();
        Set<Handler> handlers = reflections.getTypesAnnotatedWith(FusionHandler.class).stream()
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
                .peek(this::createContext)
                .collect(Collectors.toSet());
        this.handlers.addAll(handlers);
        if (!handlers.isEmpty()) {
            ConsoleUtil.info("为插件" + plugin.getName() + "注册了" + handlers.size() + "个HTTP服务.");
        }
    }

    public void createContext(@NotNull Handler handler) {
        String path = handler.getPath();
        server.createContext(path, handler);
        ConsoleUtil.info("创建服务(" + handler.getClass().getSimpleName() + ": " + handler.getPath() + ")");
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

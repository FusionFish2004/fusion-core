package cn.fusionfish.core.web.http;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.exception.HttpServerNotDeployingException;
import cn.fusionfish.core.plugin.FusionPlugin;
import cn.fusionfish.core.utils.ConsoleUtil;
import com.google.common.collect.Sets;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * @author JeremyHu
 */
@SuppressWarnings("unused")
@Getter
public final class ServerController {

    private final HttpServer server;
    private final Set<MethodRequestHandler> handlers = Sets.newHashSet();
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

        loadHandlers(FusionCore.getInstance());
    }

    public void loadHandlers(@NotNull FusionPlugin plugin) {
        Reflections reflections = plugin.getReflections();
        Set<MethodRequestHandler> handlers = Sets.newHashSet();
                reflections.getTypesAnnotatedWith(RequestHandler.class).stream()
                .map(clazz -> {
                    try {
                        return (Handler) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(HandlerParser::new)
                .map(HandlerParser::parse)
                .peek(methodRequestHandlers -> methodRequestHandlers.forEach(this::createContext))
                .forEach(handlers::addAll);

        if (!handlers.isEmpty()) {
            ConsoleUtil.info("为插件" + plugin.getName() + "注册了" + handlers.size() + "个HTTP服务.");
        }
    }

    public void createContext(@NotNull MethodRequestHandler handler) {
        String path = handler.getPath();
        server.createContext(path, handler);
        ConsoleUtil.info("创建服务(" + handler.getPath() + ")");
        handlers.add(handler);
    }

    private void createContexts() {
        handlers.forEach(this::createContext);
    }

    public void stop() {
        server.stop(0);
    }


}

package cn.fusionfish.core.web.http;

import cn.fusionfish.core.annotations.FusionHandler;
import cn.fusionfish.core.utils.ConsoleUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author JeremyHu
 */
public abstract class Handler implements HttpHandler {

    private final @NotNull String path;

    public Handler() {
        FusionHandler annotation = this.getClass().getAnnotation(FusionHandler.class);
        this.path = annotation.path();
    }

    @Override
    public final void handle(HttpExchange exchange) {
        ConsoleUtil.info("Triggered Handler(" + this.getClass().getSimpleName() + ")");
        Request request = new Request(exchange);
        handleRequest(request);
        request.respond("null");
    }

    /**
     * 处理一个HTTP请求
     * @param request 请求
     */
    protected abstract void handleRequest(Request request);

    public @NotNull String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Handler handler = (Handler) o;
        return getPath().equals(handler.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }
}

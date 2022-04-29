package cn.fusionfish.core.web.http;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author JeremyHu
 */
public class Request {
    private final HttpExchange exchange;
    private boolean responded = false;

    protected Request(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void respond(@NotNull String string) {
        if (responded) {
            return;
        }
        try {
            exchange.sendResponseHeaders(200,string.getBytes().length);
            OutputStream body = exchange.getResponseBody();
            body.write(string.getBytes());
            body.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exchange.close();
        responded = true;
    }

    public Query getQuery() {
        String queryString = exchange.getRequestURI().getQuery();
        String[] args = queryString.split("&");
        Map<String,String> map = Maps.newHashMap();
        for(String str : args){
            String[] args2 = str.split("=");
            map.put(args2[0],args2[1]);
        }
        return new Query(map);
    }

}
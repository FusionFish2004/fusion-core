package cn.fusionfish.core.web.http;

import cn.fusionfish.core.annotations.RequestParam;
import cn.fusionfish.core.command.parser.Parser;
import cn.fusionfish.core.command.parser.ParserFactory;
import cn.fusionfish.core.utils.ConsoleUtil;
import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author JeremyHu
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MethodRequestHandler implements HttpHandler {

    private final Method method;
    private final String path;
    private final Handler handler;

    @Override
    public void handle(HttpExchange exchange) {
        try {
            ConsoleUtil.info("触发服务" + path);
            Map<String, String> query = getQuery(exchange);
            //解析请求

            AnnotatedType[] annotatedParams = method.getAnnotatedParameterTypes();
            Class<?>[] params = method.getParameterTypes();

            if (annotatedParams.length != params.length) {
                //所有参数必须都带注解
                throw new IllegalArgumentException();
            }

            //参数值数组
            Object[] values = new Object[params.length];
            for (int i = 0; i < params.length; i++) {

                Annotation[] parameterAnnotations = method.getParameterAnnotations()[i];

                RequestParam annotation = (RequestParam) Arrays.stream(parameterAnnotations)
                        .filter(a -> a.annotationType().equals(RequestParam.class))
                        .findAny()
                        .orElseThrow();

                Class<?> param = params[i];

                String paramName = annotation.paramName();
                String defaultValue = annotation.defaultValue();

                //获取参数
                String value = Objects.requireNonNullElse(query.get(paramName), defaultValue);

                ParserFactory parserFactory = new ParserFactory();
                Parser<?> parser = parserFactory.get(param);
                Object parse = parser.parse(value);

                //存入数组
                values[i] = parse;
            }

            //调用方法
            String invoke = (String) method.invoke(handler, values);

            //回应请求
            respond(exchange, invoke);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static @NotNull Map<String, String> getQuery(@NotNull HttpExchange exchange) {
        String queryString = exchange.getRequestURI().getQuery();

        Map<String,String> map = Maps.newHashMap();

        if (queryString == null) {
            return map;
        }

        String[] args = queryString.split("&");
        for (String str : args) {
            String[] args2 = str.split("=");
            map.put(args2[0], args2[1]);
        }
        return map;
    }

    public static void respond(@NotNull HttpExchange exchange, @NotNull String response) {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        try (exchange) {
            exchange.getResponseHeaders().add("Content-Type:", "text/html;charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream body = exchange.getResponseBody();
            body.write(bytes);
            body.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

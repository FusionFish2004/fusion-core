package cn.fusionfish.core.web.http;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author JeremyHu
 */
@AllArgsConstructor
@Log4j2
public class HandlerParser {
    private final Handler handler;
    private final List<String> paths = Lists.newArrayList();

    public List<MethodRequestHandler> parse() {
        Class<? extends Handler> clazz = handler.getClass();
        if (!clazz.isAnnotationPresent(RequestHandler.class)) {
            //类上不包含注解
            throw new IllegalArgumentException("invalid handler: annotation not present");
        }

        RequestHandler annotation = clazz.getAnnotation(RequestHandler.class);
        assert annotation != null;
        //将地址拆分 便于操作
        List<String> typePath = Lists.newArrayList(annotation.path().split("/"));
        String pathPrefix = typePath.stream()
                .filter(s -> !"".equals(s))
                .reduce("", (s1, s2) -> s1 + "/" + s2);

        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestHandler.class))
                .peek(method -> method.setAccessible(true))
                .toList();

        List<MethodRequestHandler> handlers = Lists.newArrayList();

        for (Method method : methods) {
            RequestHandler methodAnnotation = method.getAnnotation(RequestHandler.class);
            assert methodAnnotation != null;
            StringJoiner joiner = new StringJoiner("/", pathPrefix, "");

            for (String s : methodAnnotation.path().split("/")) {
                joiner.add(s);
            }
            //解析得到每个方法对应的地址
            String path = joiner.toString();

            if (paths.contains(path)) {
                //地址已存在
                log.warn("path " + path + " already registered.");
                continue;
            }

            Class<?> returnType = method.getReturnType();
            if (!returnType.equals(String.class)) {
                log.warn("bad return type: " + returnType.getName());
                continue;
            }

            //存入地址集合
            paths.add(path);
            handlers.add(new MethodRequestHandler(method,path,handler));

        }
        return handlers;
    }

}

package cn.fusionfish.core.utils.parser;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.exception.command.ParseException;
import cn.fusionfish.core.plugin.FusionPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author JeremyHu
 */
public interface ParamParser<T> {

    String NULL_STRING = "null";

    /**
     * 解析参数
     * @param arg 参数
     * @return 数据
     * @throws ParseException 解析异常
     */
    @Nullable T parse(String arg) throws ParseException;

    /**
     * 智能解析所有参数
     * @param types 参数类型
     * @param args 参数
     * @throws ParseException 当解析失败时抛出异常
     * @return 解析后的所有参数
     */
    static Object @Nullable [] parseArgs(String[] args, String[] types) throws ParseException {

        if (args == null) {
            return null;
        }

        if (types == null) {
            return null;
        }

        ParserFactory factory = FusionCore.getParserFactory();

        if (args.length != types.length) {
            throw new IllegalArgumentException("智能解析参数数量不匹配！");
        }

        Object[] results = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            String type = types[i];
            String arg = args[i];
            ParamParser<?> paramParser = factory.get(type);
            Object result = paramParser.parse(arg);
            results[i] = result;
        }

        return results;
    }

    /**
     * 智能解析所有参数
     * @param types 参数类型
     * @param args 参数
     * @throws ParseException 当解析失败时抛出异常
     * @return 解析后的所有参数
     */
    @Contract(pure = true)
    static Object @Nullable [] parseArgs(String[] args, Class<?>[] types) throws ParseException {

        if (args == null) {
            return null;
        }

        if (types == null) {
            return null;
        }

        ParserFactory factory = new ParserFactory();

        if (args.length != types.length) {
            throw new IllegalArgumentException("智能解析参数数量不匹配！");
        }

        Object[] results = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Class<?> type = types[i];
            String arg = args[i];
            ParamParser<?> paramParser = factory.get(type);
            Object result = paramParser.parse(arg);
            results[i] = result;
        }

        return results;
    }

    default Class<?> getType() {
        try {
            Method method = getClass().getDeclaredMethod("parse", String.class);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}

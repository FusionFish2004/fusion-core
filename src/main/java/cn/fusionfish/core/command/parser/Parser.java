package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.ParseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author JeremyHu
 */
public interface Parser<T> {
    /**
     * 解析参数
     * @param arg 参数
     * @return 数据
     * @throws ParseException 解析异常
     */
    T parse(String arg) throws ParseException;

    /**
     * 智能解析所有参数
     * @param types 参数类型
     * @param args 参数
     * @throws ParseException 当解析失败时抛出异常
     * @return 解析后的所有参数
     */
    @Contract(pure = true)
    static Object @Nullable [] parseArgs(String[] args, String[] types) throws ParseException {

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
            String type = types[i];
            String arg = args[i];
            Parser<?> parser = factory.get(type);
            Object result = parser.parse(arg);
            results[i] = result;
        }

        return results;
    }
}

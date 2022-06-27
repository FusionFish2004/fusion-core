package cn.fusionfish.core.utils.parser;

import cn.fusionfish.core.plugin.FusionPlugin;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author JeremyHu
 */
public class ParserFactory {

    private final Map<Class<?>, ParamParser<?>> parserMap = Maps.newHashMap();

    public void registerParsers(@NotNull FusionPlugin plugin) {
        Reflections reflections = plugin.getReflections();
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Parser.class);
        classes.stream()
                .parallel()
                .filter(clazz -> clazz.getSuperclass().equals(ParamParser.class))
                .map(clazz -> {
                    try {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return (ParamParser<?>) constructor.newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(parser -> parserMap.put(parser.getType(), parser));
    }

    public ParamParser<?> get(@NotNull Class<?> classOfArg) {
        String nameOfType = classOfArg.getSimpleName();
        return get(nameOfType);

    }

    public ParamParser<?> get(String nameOfType) {
        return switch (nameOfType) {
            case "Integer", "int" -> new IntegerParser();
            case "Double", "double" -> new DoubleParser();
            case "Boolean", "boolean" -> new BooleanParser();
            case "Float", "float" -> new FloatParser();
            case "Long", "long" -> new LongParser();
            case "String" -> new StringParser();
            case "UUID" -> new UUIDParser();
            case "Date" -> new DateParser();
            case "Vector" -> new VectorParser();
            case "World" -> new WorldParser();
            case "Player" -> new PlayerParser();
            default -> null;
        };
    }
}

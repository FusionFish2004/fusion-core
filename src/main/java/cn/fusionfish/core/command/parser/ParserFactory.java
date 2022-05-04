package cn.fusionfish.core.command.parser;

import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class ParserFactory {
    public Parser<?> get(@NotNull Class<?> classOfArg) {
        return get(classOfArg.getSimpleName());
    }

    public Parser<?> get(String nameOfType) {
        return switch (nameOfType) {
            case "Integer", "int" -> new IntegerParser();
            case "Double", "double" -> new DoubleParser();
            case "Boolean", "boolean" -> new BooleanParser();
            case "Float", "float" -> new FloatParser();
            case "Long", "long" -> new LongParser();
            case "Date" -> new DateParser();
            case "Vector" -> new VectorParser();
            case "World" -> new WorldParser();
            default -> null;
        };
    }
}

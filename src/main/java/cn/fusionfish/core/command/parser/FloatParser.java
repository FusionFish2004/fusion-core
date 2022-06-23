package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class FloatParser implements Parser<Float> {
    @Override
    public Float parse(@NotNull String arg) throws ParseException {
        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }
        try {
            return Float.parseFloat(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

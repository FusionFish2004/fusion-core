package cn.fusionfish.core.utils.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */

public class DoubleParser implements ParamParser<Double> {
    @Override
    public Double parse(@NotNull String arg) throws ParseException {
        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }
        try {
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

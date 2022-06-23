package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class BooleanParser implements Parser<Boolean> {

    @Override
    public Boolean parse(@NotNull String arg) throws ParseException {

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        if ("true".equalsIgnoreCase(arg)) {
            return true;
        }

        if ("false".equalsIgnoreCase(arg)) {
            return false;
        }

        throw new ParseException(arg);
    }
}

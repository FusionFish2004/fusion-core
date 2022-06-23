package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class StringParser implements Parser<String> {

    @Override
    public String parse(@NotNull String arg) throws ParseException {

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        try {
            return arg;
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class LongParser implements Parser<Long> {

    @Override
    public Long parse(@NotNull String arg) throws ParseException {

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        try {
            return Long.parseLong(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

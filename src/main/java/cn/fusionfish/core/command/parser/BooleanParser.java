package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;

/**
 * @author JeremyHu
 */
public class BooleanParser implements Parser<Boolean> {
    @Override
    public Boolean parse(String arg) throws ParseException {
        if ("true".equalsIgnoreCase(arg)) {
            return true;
        }

        if ("false".equalsIgnoreCase(arg)) {
            return false;
        }

        throw new ParseException(arg);
    }
}

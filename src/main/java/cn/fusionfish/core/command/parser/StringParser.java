package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.ParseException;

/**
 * @author JeremyHu
 */
public class StringParser implements Parser<String> {

    @Override
    public String parse(String arg) throws ParseException {
        try {
            if ("null".equalsIgnoreCase(arg)) {
                return null;
            }
            return arg;
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

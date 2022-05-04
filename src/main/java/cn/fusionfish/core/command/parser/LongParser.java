package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.ParseException;

/**
 * @author JeremyHu
 */
public class LongParser implements Parser<Long> {

    @Override
    public Long parse(String arg) throws ParseException {
        try {
            return Long.parseLong(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

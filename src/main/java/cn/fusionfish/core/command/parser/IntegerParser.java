package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.ParseException;

/**
 * @author JeremyHu
 */
public class IntegerParser implements Parser<Integer> {
    @Override
    public Integer parse(String arg) throws ParseException {
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

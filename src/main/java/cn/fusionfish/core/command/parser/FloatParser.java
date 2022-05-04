package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.ParseException;

/**
 * @author JeremyHu
 */
public class FloatParser implements Parser<Float> {
    @Override
    public Float parse(String arg) throws ParseException {
        try {
            return Float.parseFloat(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

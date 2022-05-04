package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;

/**
 * @author JeremyHu
 */
public class DoubleParser implements Parser<Double> {
    @Override
    public Double parse(String arg) throws ParseException {
        try {
            return Double.parseDouble(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

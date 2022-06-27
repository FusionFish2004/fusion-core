package cn.fusionfish.core.utils.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class IntegerParser implements ParamParser<Integer> {


    @Override
    public Integer parse(@NotNull String arg) throws ParseException {
        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

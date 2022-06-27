package cn.fusionfish.core.utils.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author JeremyHu
 */
public class DateParser implements ParamParser<Date> {
    private static final String[] PATTERNS = {
            "yyyy-MM-dd hh:mm:ss",
            "yyyy-MM-dd hh:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd hh:mm:ss",
            "yyyy/MM/dd hh:mm",
            "yyyy/MM/dd"
    };

    @Override
    public Date parse(@NotNull String arg) throws ParseException{

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        SimpleDateFormat simpleDateFormat;

        if ("now".equalsIgnoreCase(arg)) {
            return new Date();
        }

        try {
            long time = Long.parseLong(arg);
            return new Date(time);
        } catch (NumberFormatException ignored) {

        }

        for (String pattern : PATTERNS) {
            simpleDateFormat = new SimpleDateFormat(pattern);
            try {
                return simpleDateFormat.parse(arg);
            } catch (Exception ignored) {

            }
        }
        throw new ParseException(arg);
    }
}

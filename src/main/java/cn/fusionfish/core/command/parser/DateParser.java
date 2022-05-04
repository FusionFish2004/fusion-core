package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author JeremyHu
 */
public class DateParser implements Parser<Date> {
    private static final String[] PATTERNS = {
            "yyyy-MM-dd hh:mm:ss",
            "yyyy-MM-dd hh:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd hh:mm:ss",
            "yyyy/MM/dd hh:mm",
            "yyyy/MM/dd"
    };

    @Override
    public Date parse(String arg) throws ParseException{

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

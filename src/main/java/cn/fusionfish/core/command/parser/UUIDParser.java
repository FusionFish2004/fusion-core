package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author JeremyHu
 */
public class UUIDParser implements Parser<UUID> {
    @Override
    public UUID parse(@NotNull String arg) throws ParseException {

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        try {
            return UUID.fromString(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

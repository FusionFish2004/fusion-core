package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;

import java.util.UUID;

public class UUIDParser implements Parser<UUID> {
    @Override
    public UUID parse(String arg) throws ParseException {
        try {
            return UUID.fromString(arg);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}

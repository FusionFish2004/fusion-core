package cn.fusionfish.core.exception;

/**
 * @author JeremyHu
 */
public class ParseException extends Exception {
    private final String arg;

    public ParseException(String arg) {
        this.arg = arg;
    }

    public String getArg() {
        return arg;
    }
}

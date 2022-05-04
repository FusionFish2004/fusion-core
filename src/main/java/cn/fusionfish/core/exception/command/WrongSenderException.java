package cn.fusionfish.core.exception.command;

/**
 * @author JeremyHu
 */
public class WrongSenderException extends Exception {
    private final String expected;

    public WrongSenderException(String expected) {
        this.expected = expected;
    }

    public String getExpected() {
        return expected;
    }
}

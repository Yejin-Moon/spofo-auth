package net.spofo.auth.exception;

public class InvalidTokenException extends CodeException {

    public static final CodeException EXCEPTION = new InvalidTokenException();

    private InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}

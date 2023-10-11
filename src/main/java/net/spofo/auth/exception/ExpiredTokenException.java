package net.spofo.auth.exception;

public class ExpiredTokenException extends CodeException {

    public static final CodeException EXCEPTION = new ExpiredTokenException();

    private ExpiredTokenException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
}

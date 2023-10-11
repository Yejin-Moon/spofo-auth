package net.spofo.auth.exception;

public class ExpiredTokenException extends CodeException {

    public ExpiredTokenException() {
        super(ErrorCode.TOKEN_EXPIRED);
    }
}

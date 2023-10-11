package net.spofo.auth.exception;

public class InvalidTokenException extends CodeException {

    public InvalidTokenException() { super(ErrorCode.INVALID_TOKEN); }
}

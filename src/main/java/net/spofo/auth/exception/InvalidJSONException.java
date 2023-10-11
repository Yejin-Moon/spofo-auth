package net.spofo.auth.exception;

public class InvalidJSONException extends CodeException {

    public static final CodeException EXCEPTION = new InvalidJSONException();

    private InvalidJSONException() {
        super(ErrorCode.SERVER_ERROR);
    }
}


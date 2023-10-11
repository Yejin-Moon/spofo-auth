package net.spofo.auth.exception;

public class InvalidJSONException extends CodeException {

    public InvalidJSONException() {
        super(ErrorCode.SERVER_ERROR);
    }
}


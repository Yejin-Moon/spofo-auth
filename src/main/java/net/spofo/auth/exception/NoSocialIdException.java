package net.spofo.auth.exception;

public class NoSocialIdException extends CodeException {

    public NoSocialIdException() {
        super(ErrorCode.ID_NOT_EXIST);
    }
}


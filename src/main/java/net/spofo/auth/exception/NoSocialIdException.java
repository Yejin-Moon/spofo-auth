package net.spofo.auth.exception;

public class NoSocialIdException extends CodeException {

    public static final CodeException EXCEPTION = new NoSocialIdException();

    private NoSocialIdException() {
        super(ErrorCode.ID_NOT_EXIST);
    }
}


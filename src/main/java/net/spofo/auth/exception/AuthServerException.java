package net.spofo.auth.exception;

import org.springframework.http.HttpStatus;

public abstract class AuthServerException extends RuntimeException {

    public abstract HttpStatus getStatusCode();

    public AuthServerException(String message) {
        super(message);
    }

}

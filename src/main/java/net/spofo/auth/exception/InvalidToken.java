package net.spofo.auth.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;

public class InvalidToken extends AuthServerException {

    public InvalidToken(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return UNAUTHORIZED;
    }
}

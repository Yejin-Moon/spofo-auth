package net.spofo.auth.exception.advice;

import static org.springframework.http.ResponseEntity.status;

import net.spofo.auth.exception.AuthServerException;
import net.spofo.auth.exception.dto.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> commonExHandler(AuthServerException e) {
        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(e.getStatusCode())
                .errorMessage(e.getMessage())
                .build();

        return status(e.getStatusCode()).body(errorResult);
    }
}
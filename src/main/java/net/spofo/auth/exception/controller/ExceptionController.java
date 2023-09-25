package net.spofo.auth.exception.controller;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.exception.dto.ErrorResult;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final MessageSource messageSource;

    @ResponseBody
    @ExceptionHandler
    public ResponseEntity<ErrorResult> invalidRequestHandler(IllegalArgumentException e) {
        String errorMessage = e.getMessage();
        ErrorResult errorResult = ErrorResult.builder()
                .errorCode(BAD_REQUEST)
                .errorMessage(errorMessage)
                .build();
        return new ResponseEntity<>(errorResult, BAD_REQUEST);
    }
}

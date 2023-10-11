package net.spofo.auth.exception.advice;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.exception.CodeException;
import net.spofo.auth.exception.dto.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {
    @ExceptionHandler
    public ResponseEntity<ErrorResult> invalidRequestHandler(CodeException e) {
        int status = e.getErrorResult().getStatus();
        String errorCode = e.getErrorResult().getCode();
        String errorMsg = e.getErrorResult().getReason();
        ErrorResult errorResult = ErrorResult.builder()
                .status(status)
                .code(errorCode)
                .reason(errorMsg)
                .build();
        return ResponseEntity.status(status).body(errorResult);
    }
}

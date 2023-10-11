package net.spofo.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.spofo.auth.exception.dto.ErrorResult;

@Getter
@AllArgsConstructor
public class CodeException extends RuntimeException {

    private BaseErrorCode errorCode;

    public ErrorResult getErrorResult() {
        return this.errorCode.getErrorResult();
    }
}
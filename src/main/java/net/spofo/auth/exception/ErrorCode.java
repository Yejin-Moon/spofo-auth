package net.spofo.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.spofo.auth.exception.dto.ErrorResult;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {
    TOKEN_EXPIRED(401, "401_1", "만료된 토큰입니다."),
    INVALID_TOKEN(401, "401_2", "유효하지 않은 토큰입니다."),
    ID_NOT_EXIST(401, "401_3", "아이디가 존재하지 않습니다."),
    SERVER_ERROR(500, "500_1", "서버 문제가 발생했습니다.");

    private Integer status;
    private String code;
    private String reason;

    @Override
    public ErrorResult getErrorResult() {
        return ErrorResult.builder()
                .reason(reason).code(code).status(status).build();
    }
}

package net.spofo.auth.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResult {

    private final Integer status;
    private final String code;
    private final String reason;
}
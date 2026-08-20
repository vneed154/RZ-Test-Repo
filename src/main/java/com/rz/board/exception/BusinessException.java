package com.rz.board.exception;

import lombok.Getter;

/**
 * 도메인 규칙 위반(존재하지 않는 리소스, 중복 등)을 나타내는 공통 예외.
 * 서비스 계층은 항상 이 예외 + ErrorCode 조합으로 던지고,
 * GlobalExceptionHandler가 이를 일관된 응답 형식으로 변환한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

package com.tars.app.application.exception

import com.vito.common.error.ErrorMessage

/**
 * 비즈니스 로직 관련 예외
 * 
 * 비즈니스 규칙 위반이나 도메인 로직 오류 등 비즈니스 관련 예외를 표현합니다.
 * 이 예외는 클라이언트에게 의미 있는 오류 메시지를 전달하기 위해 사용됩니다.
 */
class BusinessException(
    val errorMessage: ErrorMessage,
    val args: Array<out Any> = emptyArray(),
    cause: Throwable? = null
) : RuntimeException(errorMessage.format(*args), cause) {
    
    /**
     * 에러 코드를 반환합니다.
     */
    val errorCode: String
        get() = errorMessage.code
    
    /**
     * 포맷팅된 에러 메시지를 반환합니다.
     */
    val formattedMessage: String
        get() = errorMessage.format(*args)
} 
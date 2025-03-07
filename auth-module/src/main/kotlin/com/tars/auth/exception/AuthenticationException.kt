package com.tars.auth.exception

/**
 * 인증 과정에서 발생하는 예외를 나타내는 클래스
 * 
 * @param message 예외 메시지
 * @param cause 원인 예외
 */
class AuthenticationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
package com.tars.common.exception

import java.time.LocalDateTime

/**
 * API 오류 응답 형식
 */
data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val code: String,
    val message: String,
    val path: String
) 
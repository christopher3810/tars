package com.tars.auth.dto

/**
 * 토큰에 포함될 사용자 정보
 */
data class UserTokenInfo(
    val id: Long,
    val email: String,
    val roles: Set<String> = emptySet(),
    val additionalClaims: Map<String, Any> = emptyMap()
) 
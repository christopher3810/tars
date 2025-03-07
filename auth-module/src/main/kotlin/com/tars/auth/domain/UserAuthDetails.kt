package com.tars.auth.domain

/**
 * 인증에 필요한 사용자 정보를 담는 도메인 클래스
 */
data class UserAuthDetails(
    val id: Long,
    val email: String,
    val hashedPassword: String,
    val roles: Set<String>
) 
package com.tars.auth.port

import com.tars.auth.domain.UserAuthDetails

/**
 * 사용자 인증을 위한 포트 인터페이스
 * 애플리케이션 모듈에서 구현해야 합니다.
 */
interface UserAuthPort {
    /**
     * 이메일로 사용자 인증 정보를 조회합니다.
     */
    fun findUserByEmail(email: String): UserAuthDetails?
    
    /**
     * 사용자 ID로 사용자 인증 정보를 조회합니다.
     */
    fun findUserById(id: Long): UserAuthDetails?
} 
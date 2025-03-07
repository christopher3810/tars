package com.tars.app.outport.user

import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.user.User

/**
 * 사용자 저장소 포트
 * 도메인 계층에서 정의된 인터페이스로, 외부 저장소와의 상호작용을 추상화
 */
interface UserRepositoryPort {

    fun save(userDomain: User): UserEntity?

    fun findByEmail(email: String): UserEntity?

    fun findById(id: Long): UserEntity?

}
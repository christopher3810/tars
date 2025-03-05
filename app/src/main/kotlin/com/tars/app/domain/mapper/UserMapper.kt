package com.tars.app.domain.mapper

import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.user.User
import org.springframework.stereotype.Component

/**
 * UserMapper는 도메인 객체에서 엔티티로의 변환만 담당합니다.
 * 엔티티에서 도메인 객체로의 변환은 도메인 서비스의 책임입니다.
 */
@Component
class UserMapper {
    /**
     * 도메인 객체에서 Entity로 변환합니다.
     * 이 메서드는 영속성 계층에 저장하기 위한 용도로만 사용됩니다.
     */
    fun toEntity(domain: User): UserEntity =
        UserEntity(
            id = domain.id,
            email = domain.credentials.email,
            password = domain.credentials.hashedPassword,
            ssn = domain.credentials.ssn,
            phoneNumber = domain.credentials.phoneNumber,
            name = domain.credentials.name,
            address = domain.getAddress(),
            birthDate = domain.credentials.birthDate,
            roles = domain.getRoles()
        )
}
package com.tars.app.domain.mapper

import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.user.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toDomain(entity: UserEntity): User =
        User(
            id = entity.id,
            email = entity.email,
            password = entity.password,
            ssn = entity.ssn,
            phoneNumber = entity.phoneNumber,
            name = entity.name,
            address = entity.address,
            birthDate = entity.birthDate,
            roles = entity.roles
        )

    fun toEntity(domain: User): UserEntity =
        UserEntity(
            id = domain.id,
            email = domain.email,
            password = domain.password,
            ssn = domain.ssn,
            phoneNumber = domain.phoneNumber,
            name = domain.name,
            address = domain.address,
            birthDate = domain.birthDate,
            roles = domain.roles
        )
}
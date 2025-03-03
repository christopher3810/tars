package com.tars.app.outport.user

import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.user.User

interface UserRepositoryPort {
    fun save(userDomain: User):UserEntity?
    fun findByEmail(email: String): User?
    fun findById(id: Long): User?
}
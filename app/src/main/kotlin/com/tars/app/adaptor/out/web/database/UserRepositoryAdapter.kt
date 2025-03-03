package com.tars.app.adaptor.out.web.database

import com.tars.app.adaptor.out.web.database.repository.JpaUserRepository
import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.mapper.UserMapper
import com.tars.app.domain.user.User
import com.tars.app.outport.user.UserRepositoryPort
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(
    private val userRepository: JpaUserRepository,
    private val userMapper: UserMapper
) : UserRepositoryPort {

    override fun save(userDomain: User): UserEntity? {
        val entity = userMapper.toEntity(userDomain)
        return userRepository.save(entity)
    }

    //TODO : Mapper 로 바로 Domain 만드는데 이거 Factory 구조 정해지면 여긴 Entity 만 반환하도록 변경
    override fun findByEmail(email: String): User? {
        val entity = userRepository.findByEmail(email)
        return entity?.let(userMapper::toDomain)
    }

    override fun findById(id: Long): User? {
        val entity = userRepository.findById(id).orElse(null)
        return entity?.let(userMapper::toDomain)
    }
}
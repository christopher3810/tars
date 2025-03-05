package com.tars.app.adaptor.out.web.database

import com.tars.app.adaptor.out.web.database.repository.JpaUserRepository
import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.factory.UserFactory
import com.tars.app.domain.mapper.UserMapper
import com.tars.app.domain.user.User
import com.tars.app.outport.user.UserRepositoryPort
import org.springframework.stereotype.Component

/**
 * UserRepositoryAdapter는 영속성 계층과 도메인 계층 사이의 어댑터입니다.
 * 이 어댑터는 JPA 저장소와 연결하며, 도메인 계층에 정의된 포트를 구현합니다.
 * 도메인 객체 생성은 Factory를 통해 이루어집니다.
 */
@Component
class UserRepositoryAdapter(
    private val userRepository: JpaUserRepository,
    private val userMapper: UserMapper,
    private val userFactory: UserFactory
) : UserRepositoryPort {

    override fun save(userDomain: User): UserEntity? {
        val entity = userMapper.toEntity(userDomain)
        return userRepository.save(entity)
    }

    override fun findByEmail(email: String): User? {
        val entity = userRepository.findByEmail(email) ?: return null
        return userFactory.reconstitute(entity)
    }

    override fun findById(id: Long): User? {
        val entity = userRepository.findById(id).orElse(null) ?: return null
        return userFactory.reconstitute(entity)
    }

}
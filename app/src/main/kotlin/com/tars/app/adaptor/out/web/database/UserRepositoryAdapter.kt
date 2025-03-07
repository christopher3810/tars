package com.tars.app.adaptor.out.web.database

import com.tars.app.adaptor.out.web.database.repository.JpaUserRepository
import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.mapper.UserMapper
import com.tars.app.domain.user.User
import com.tars.app.outport.user.UserRepositoryPort
import org.springframework.stereotype.Component

/**
 * UserRepositoryAdapter 는 영속성 계층과 도메인 계층 사이의 어댑터
 * 이 어댑터는 JPA 저장소와 연결하며, 도메인 계층에 정의된 포트를 구현.
 * 어댑터는 엔티티 객체만 반환하고, 도메인 객체 생성은 애플리케이션 서비스 책임입니다.
 */
@Component
class UserRepositoryAdapter(
    private val userRepository: JpaUserRepository,
    private val userMapper: UserMapper
) : UserRepositoryPort {

    override fun save(userDomain: User): UserEntity? {
        val entity = userMapper.toEntity(userDomain)
        return userRepository.save(entity)
    }

    override fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    override fun findById(id: Long): UserEntity? {
        return userRepository.findById(id).orElse(null)
    }

}
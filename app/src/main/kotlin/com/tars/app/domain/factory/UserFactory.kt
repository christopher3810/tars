package com.tars.app.domain.factory

import com.tars.app.domain.entity.UserEntity
import com.tars.app.domain.user.User
import com.tars.app.domain.user.vo.UserCredentials
import com.tars.app.event.UserEvent
import com.tars.app.outport.event.EventPublisherPort
import org.springframework.stereotype.Component
import java.time.LocalDate


@Component
class UserFactory(
    private val eventPublisher: EventPublisherPort
) {
    /**
     * 신규 사용자 생성
     * ROLE_USER 부여, 생성 이벤트가 발행.
     * 10자리 고유 ID는 User.create 내부에서 자동 생성.
     */
    fun createUser(
        email: String,
        rawPassword: String,
        ssn: String,
        phoneNumber: String,
        name: String,
        address: String? = null,
        birthDate: LocalDate? = null
    ): User {
        val credentials = UserCredentials.create(
            email = email,
            rawPassword = rawPassword,
            ssn = ssn,
            phoneNumber = phoneNumber,
            name = name,
            birthDate = birthDate
        )

        val user = User.create(
            credentials = credentials,
            address = address,
            roles = setOf(User.ROLE_USER)
        )

        // 도메인 이벤트 발행
        eventPublisher.publish(
            UserEvent.UserCreated(
                email = email,
                name = name
            )
        )

        return user
    }

    /**
     * 관리자 사용자 생성
     * ROLE_ADMIN이 추가로 부여.
     */
    fun createAdminUser(
        email: String,
        rawPassword: String,
        ssn: String,
        phoneNumber: String,
        name: String,
        address: String? = null,
        birthDate: LocalDate? = null
    ): User {
        val user = createUser(
            email = email,
            rawPassword = rawPassword,
            ssn = ssn,
            phoneNumber = phoneNumber,
            name = name,
            address = address,
            birthDate = birthDate
        )

        // 관리자 권한 추가
        user.addRole(User.ROLE_ADMIN)

        return user
    }

    /**
     * 기존 사용자 데이터로부터 User 객체 재구성
     */
    fun reconstitute(entity: UserEntity): User {
        val credentials = UserCredentials.reconstitute(
            email = entity.email,
            hashedPassword = entity.password,
            ssn = entity.ssn,
            phoneNumber = entity.phoneNumber,
            name = entity.name,
            birthDate = entity.birthDate
        )

        return User.reconstitute(
            id = entity.id,
            credentials = credentials,
            address = entity.address,
            roles = entity.roles
        )
    }
} 
package com.tars.app.domain.factory

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
        // UserCredentials Value Object 생성
        val credentials = UserCredentials.create(
            email = email,
            rawPassword = rawPassword,
            ssn = ssn,
            phoneNumber = phoneNumber,
            name = name,
            birthDate = birthDate
        )

        // User 애그리거트 생성
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
    fun reconstitute(
        id: Long,
        email: String,
        hashedPassword: String,
        ssn: String,
        phoneNumber: String,
        name: String,
        address: String?,
        birthDate: LocalDate?,
        roles: Set<String>
    ): User {
        val credentials = UserCredentials.fromExisting(
            email = email,
            hashedPassword = hashedPassword,
            ssn = ssn,
            phoneNumber = phoneNumber,
            name = name,
            birthDate = birthDate
        )

        return User.reconstitute(
            id = id,
            credentials = credentials,
            address = address,
            roles = roles
        )
    }
} 
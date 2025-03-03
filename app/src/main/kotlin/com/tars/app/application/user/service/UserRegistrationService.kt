package com.tars.app.application.user.service

import com.tars.app.application.user.UserRegistrationUseCase
import com.tars.app.application.validator.ValidationField
import com.tars.app.application.validator.policy.EmailDuplicationPolicy
import com.tars.app.application.validator.policy.PolicyBuilder
import com.tars.app.domain.factory.UserFactory
import com.tars.app.outport.user.UserRepositoryPort
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserRegistrationService(
    private val userRepositoryPort: UserRepositoryPort,
    private val userFactory: UserFactory,
    private val emailDuplicationPolicy: EmailDuplicationPolicy
) : UserRegistrationUseCase {

    //TODO : Thread Per Request 구조인데 Coroutine 쓰면 비동기 non blocking 으로 해볼만 한듯?
    @Transactional
    override fun registerUser(
        email: String,
        rawPassword: String,
        ssn: String,
        phone: String,
        name: String,
        address: String?,
        birthDate: String?
    ): UserRegistrationUseCase.Response {

        validateEmailDuplication(email, ssn)

        //TODO : 여기 factory 사용하지 않고 유저 저장할만 한것 같은데 도메인 만들어야 하나 고민중
        val userDomain = userFactory.createUser(
            email = email,
            rawPassword = rawPassword,
            ssn = ssn,
            phoneNumber = phone,
            name = name,
            address = address,
            birthDateString = birthDate
        )

        val entity = userRepositoryPort.save(userDomain)
        val userId = entity?.id
        return UserRegistrationUseCase.Response(userId)
    }

    private fun validateEmailDuplication(email: String, ssn: String) {
        val validationInput = mapOf(
            ValidationField.EMAIL.key to email,
            ValidationField.SSN.key to ssn,
        )

        val validator = PolicyBuilder()
            .addPolicy(emailDuplicationPolicy)
            .build()
        validator.validate(validationInput)
    }
}
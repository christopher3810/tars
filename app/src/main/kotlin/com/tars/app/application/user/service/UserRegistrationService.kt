package com.tars.app.application.user.service

import com.tars.app.adaptor.`in`.web.controller.user.dto.UserRegistrationDto
import com.tars.app.application.user.UserRegistrationUseCase
import com.tars.app.application.validator.ValidationField
import com.tars.app.application.validator.policy.EmailDuplicationPolicy
import com.tars.app.application.validator.policy.PolicyBuilder
import com.tars.app.config.CoroutineDispatcherProvider
import com.tars.app.domain.factory.UserFactory
import com.tars.app.outport.user.UserRepositoryPort
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegistrationService(
    private val userRepositoryPort: UserRepositoryPort,
    private val userFactory: UserFactory,
    private val emailDuplicationPolicy: EmailDuplicationPolicy,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : UserRegistrationUseCase {

    @Transactional
    override suspend fun registerUser(dto: UserRegistrationDto): UserRegistrationUseCase.Response {
        return coroutineScope {
            // async 로 수행
            val validationDeferred = async(dispatcherProvider.io) {
                validateEmailDuplication(dto.email, dto.ssn)
            }

            // 사용자 도메인 객체 생성 (CPU 바운드)
            val user = withContext(dispatcherProvider.default) {
                userFactory.createUser(
                    email = dto.email,
                    rawPassword = dto.password,
                    ssn = dto.ssn,
                    phoneNumber = dto.phoneNumber,
                    name = dto.name,
                    address = dto.address,
                    birthDate = dto.birthDate
                )
            }

            // 검증 완료 대기
            validationDeferred.await()

            // DB 저장 (IO 바운드)
            val savedUser = withContext(dispatcherProvider.io) {
                userRepositoryPort.save(user)
            }

            UserRegistrationUseCase.Response(savedUser?.id)
        }
    }

    private suspend fun validateEmailDuplication(email: String, ssn: String) {
        val validationInput = mapOf(
            ValidationField.EMAIL.key to email,
            ValidationField.SSN.key to ssn,
        )

        withContext(dispatcherProvider.io) {
            val validator = PolicyBuilder()
                .addPolicy(emailDuplicationPolicy)
                .build()
            validator.validate(validationInput)
        }
    }
}
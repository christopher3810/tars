package com.tars.app.application.user.service

import com.tars.app.application.user.UserRegistrationUseCase
import com.tars.app.application.validator.ValidationField
import com.tars.app.application.validator.policy.EmailDuplicationPolicy
import com.tars.app.application.validator.policy.PolicyBuilder
import com.tars.app.config.CoroutineDispatcherProvider
import com.tars.app.domain.factory.UserFactory
import com.tars.app.event.UserEvent
import com.tars.app.outport.event.EventPublisherPort
import com.tars.app.outport.user.UserRepositoryPort
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegistrationService(
    private val userRepositoryPort: UserRepositoryPort,
    private val userFactory: UserFactory,
    private val emailDuplicationPolicy: EmailDuplicationPolicy,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val eventPublisher: EventPublisherPort
) : UserRegistrationUseCase {

    @Transactional
    override suspend fun registerUser(request: UserRegistrationUseCase.Request): UserRegistrationUseCase.Response {
        return coroutineScope {
            // async 로 수행
            val validationDeferred = async(dispatcherProvider.io) {
                validateEmailDuplication(request.email, request.ssn)
            }

            // 사용자 도메인 객체 생성 (CPU 바운드)
            val user = withContext(dispatcherProvider.default) {
                userFactory.createUser(
                    email = request.email,
                    rawPassword = request.password,
                    ssn = request.ssn,
                    phoneNumber = request.phoneNumber,
                    name = request.name,
                    address = request.address,
                    birthDate = request.birthDate
                )
            }

            // 검증 완료 대기
            validationDeferred.await()

            // DB 저장 (IO 바운드)
            val savedEntity = withContext(dispatcherProvider.io) {
                userRepositoryPort.save(user)
            }

            // 엔티티가 저장되었다면 ID를 가져오고, 아니면 null을 반환
            val userId = savedEntity?.id

            // 저장이 완료된 후 도메인 이벤트 발행
            withContext(dispatcherProvider.default) {
                if (userId != null) {
                    publishUserCreatedEvent(request.email, request.name)
                }
            }

            UserRegistrationUseCase.Response(userId)
        }
    }

    private fun publishUserCreatedEvent(email: String, name: String) {
        eventPublisher.publish(
            UserEvent.UserCreated(
                email = email,
                name = name
            )
        )
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
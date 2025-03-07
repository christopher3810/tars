package com.tars.app.application.user.service

import com.tars.app.adaptor.`in`.auth.AuthServiceAdapter
import com.tars.app.application.exception.BusinessException
import com.tars.app.application.user.LoginUseCase
import com.tars.app.config.CoroutineDispatcherProvider
import com.tars.app.domain.factory.UserFactory
import com.tars.app.outport.user.UserRepositoryPort
import com.tars.common.error.ErrorMessage
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * 로그인 유스케이스 구현체
 */
@Service
class LoginService(
    private val authServiceAdapter: AuthServiceAdapter,
    private val userRepositoryPort: UserRepositoryPort,
    private val userFactory: UserFactory,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : LoginUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자 로그인 처리
     * 
     * @param request 로그인 요청
     * @return 로그인 응답
     */
    @Transactional(readOnly = true)
    override suspend fun login(request: LoginUseCase.Request): LoginUseCase.Response {
        return withContext(dispatcherProvider.io) {
            try {
                // 사용자 엔티티 조회
                val userEntity = userRepositoryPort.findByEmail(request.email)
                    ?: throw BusinessException(ErrorMessage.INVALID_CREDENTIALS)
                
                // 엔티티를 도메인 객체로 변환 (Factory를 통해 도메인 객체 재구성)
                val user = withContext(dispatcherProvider.default) {
                    userFactory.reconstitute(userEntity)
                }
                
                // 로그인 처리 및 토큰 생성
                val tokenResponse = authServiceAdapter.login(
                    email = request.email,
                    password = request.password,
                    userId = user.id,
                    roles = user.getRoles(),
                    hashedPassword = user.credentials.hashedPassword
                )
                
                // 응답 생성
                LoginUseCase.Response(
                    userId = user.id,
                    email = user.credentials.email,
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken,
                    expiresIn = tokenResponse.expiresIn
                )

            } catch (e: Exception) {
                log.error("로그인 처리 중 오류 발생", e)
                when (e) {
                    // 네트워크 관련 예외
                    is ConnectException, is SocketTimeoutException -> {
                        throw BusinessException(ErrorMessage.INTERNAL_ERROR, cause = e)
                    }
                    // 비즈니스 예외는 그대로 전달
                    is BusinessException -> throw e
                    // 기타 예외
                    else -> {
                        throw BusinessException(ErrorMessage.INVALID_CREDENTIALS, cause = e)
                    }
                }
            }
        }
    }
}
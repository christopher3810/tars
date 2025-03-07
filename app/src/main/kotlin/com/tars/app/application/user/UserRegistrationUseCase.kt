package com.tars.app.application.user

import java.time.LocalDate

/**
 * 사용자 등록 유스케이스 인터페이스
 */
interface UserRegistrationUseCase {

    /**
     * 사용자 등록 처리
     * 
     * @param request 사용자 등록 요청
     * @return 사용자 등록 응답
     */
    suspend fun registerUser(request: Request): Response

    /**
     * 사용자 등록 요청
     */
    data class Request(
        val email: String,
        val password: String,
        val ssn: String,
        val phoneNumber: String,
        val name: String,
        val address: String?,
        val birthDate: LocalDate?
    ) {
        companion object {
            fun create(
                email: String,
                password: String,
                ssn: String,
                phoneNumber: String,
                name: String,
                address: String? = null,
                birthDate: String? = null
            ): Request {
                require(email.isNotBlank()) { "이메일은 필수입니다." }
                require(password.isNotBlank()) { "비밀번호는 필수입니다." }
                require(ssn.isNotBlank()) { "주민번호는 필수입니다." }
                require(phoneNumber.isNotBlank()) { "전화번호는 필수입니다." }
                require(name.isNotBlank()) { "이름은 필수입니다." }

                return Request(
                    email = email.trim(),
                    password = password,
                    ssn = ssn.trim(),
                    phoneNumber = phoneNumber.trim(),
                    name = name.trim(),
                    address = address?.trim(),
                    birthDate = birthDate?.let { LocalDate.parse(it) }
                )
            }
        }
    }

    /**
     * 사용자 등록 응답
     */
    data class Response(
        val userId: Long?
    )
}
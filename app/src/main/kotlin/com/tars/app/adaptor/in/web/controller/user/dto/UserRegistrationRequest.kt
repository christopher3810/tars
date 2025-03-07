package com.tars.app.adaptor.`in`.web.controller.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 사용자 등록 요청 DTO
 */
data class UserRegistrationRequest(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String,
    
    @field:NotBlank(message = "주민번호는 필수입니다.")
    val ssn: String,
    
    @field:NotBlank(message = "전화번호는 필수입니다.")
    val phoneNumber: String,
    
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,
    
    val address: String?,
    val birthDate: String?
) 
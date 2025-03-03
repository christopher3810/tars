package com.tars.app.application.validator.policy

import com.tars.app.application.validator.ValidationField
import com.tars.app.outport.user.UserRepositoryPort
import com.tars.app.util.MaskingUtil
import org.springframework.stereotype.Component

/**
 * Spring Dependency 가 필요한 Policy
 */
@Component
class EmailDuplicationPolicy(
    private val userRepositoryPort: UserRepositoryPort
) : ValidationPolicy {

    override fun validate(input: Map<String, Any?>) {
        val email = getInputValue(input, ValidationField.EMAIL.key, "email 값이 누락되었습니다.")
        val ssn = getInputValue(input, ValidationField.SSN.key, "ssn 값이 누락되었습니다.")

        userRepositoryPort.findByEmail(email)?.let {
            throw IllegalArgumentException("회원 가입을 할수 없습니다 이미 존재하는 이메일 입니다 : ${email}," +
                    "요청자 : ${MaskingUtil.maskSSN(ssn)}")
        }
    }

    private fun getInputValue(input: Map<String, Any?>, key: String, errorMessage: String): String {
        return input[key] as? String ?: throw IllegalArgumentException(errorMessage)
    }

}
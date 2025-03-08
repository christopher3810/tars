package com.tars.app.domain.user.vo

import com.tars.common.error.ErrorMessage
import com.tars.common.util.patternValidator.PatternValidator

data class SSN private constructor(val value: String) {
    companion object {
        fun of(ssn: String): SSN {
            require(PatternValidator.isValidSSN(ssn)) { ErrorMessage.INVALID_SSN_FORMAT.message }
            return SSN(ssn)
        }
        
        /**
         * 마스킹된 SSN에서 원본 SSN을 복원
         * 
         * @param maskedSsn 마스킹된 SSN (예: "123456-1******")
         * @param originalSsn 원본 SSN (예: "123456-1234567")
         * @return 원본 SSN을 나타내는 SSN 객체
         */
        fun unmask(maskedSsn: String, originalSsn: String): SSN {
            val maskedPrefix = maskedSsn.split("-")[0]
            val originalPrefix = originalSsn.split("-")[0]
            
            require(maskedPrefix == originalPrefix) { "마스킹된 SSN과 원본 SSN의 접두사가 일치하지 않습니다." }

            require(PatternValidator.isValidSSN(originalSsn)) { ErrorMessage.INVALID_SSN_FORMAT.message }
            
            return SSN(originalSsn)
        }
    }

    fun getMasked(): String {
        val parts = value.split("-")
        return "${parts[0]}-${parts[1].first()}******"
    }
} 
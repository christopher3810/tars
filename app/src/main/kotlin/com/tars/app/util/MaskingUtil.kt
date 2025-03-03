package com.tars.app.util

object MaskingUtil {

    /**
     * 주민등록번호 마스킹 (예: 123456-1234567 -> 123456-1******)
     */
    fun maskSSN(ssn: String): String {
        val parts = ssn.split("-")
        if (parts.size != 2) return ssn
        val prefix = parts[0]
        val suffix = parts[1]
        return "$prefix-${hideLastNchars(suffix, suffix.length - 1)}"
    }

    private fun hideLastNchars(input: String, hideCount: Int): String {
        val visibleLen = input.length - hideCount
        return input.substring(0, visibleLen) + "*".repeat(hideCount)
    }

    /**
     * 휴대폰번호 마스킹 (예: 010-1234-5678 -> 010-1234-****
     * 단순 예시
     */
    fun maskPhone(phone: String): String {
        if (phone.length < 4) return phone
        val cutIndex = phone.length - 4
        val prefix = phone.substring(0, cutIndex)
        val masked = "****"
        return prefix + masked
    }

}
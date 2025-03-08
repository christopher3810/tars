package com.tars.common.util.masking

import com.tars.common.util.patternValidator.PatternValidator

/**
 * 개인정보 마스킹을 위한 유틸리티 클래스
 * 
 * 이 클래스는 주민등록번호, 전화번호, 이름, 이메일 등 개인정보를 마스킹하는 기능을 제공합니다.
 * 
 * 사용 예시:
 * ```
 * val maskedSSN = MaskingUtil.maskSSN("123456-1234567", true) // "123456-1******"
 * val maskedPhone = MaskingUtil.maskPhone("010-1234-5678") // "010-1234-****"
 * val maskedName = MaskingUtil.maskName("홍길동", MaskingStrategy.SHOW_FIRST_LAST) // "홍*동"
 * val maskedEmail = MaskingUtil.maskEmail("example@domain.com") // "exa***@domain.com"
 * val maskedCard = MaskingUtil.maskCardNumber("1234-5678-9012-3456") // "1234-****-****-3456"
 * ```
 */
object MaskingUtil {

    object Constants {
        // 문자 상수
        const val MASK_CHAR = '*'
        
        // 길이 상수
        const val PHONE_LAST_DIGITS = 4
        const val CARD_FIRST_VISIBLE = 4
        const val CARD_LAST_VISIBLE = 4
        
        // 마스킹 문자열
        const val FOUR_DIGIT_MASK = "****"
        
        // 정규식 패턴
        const val NUMERIC_ONLY_PATTERN = "[^0-9]"
        const val PHONE_PATTERN = "(\\d{2,3}[-. ]?\\d{3,4}[-. ]?)(\\d{4})"
    }

    /**
     * 마스킹 전략을 정의하는 열거형
     */
    enum class MaskingStrategy {
        SHOW_FIRST_ONLY,    // 첫 글자만 표시 (예: 홍**)
        SHOW_LAST_ONLY,     // 마지막 글자만 표시 (예: **동)
        SHOW_FIRST_LAST,    // 첫글자와 마지막 글자만 표시 (예: 홍**동)
        MASK_LAST_N,        // 마지막 N개 문자 마스킹
        MASK_MIDDLE         // 중간 부분 마스킹 (예: 홍**동)
    }

    /**
     * 주민등록번호를 마스킹합니다.
     * 
     * 주민등록번호의 뒷자리 중 첫 번째 자리를 제외한 나머지를 '*'로 대체합니다.
     * 
     * @param ssn 마스킹할 주민등록번호 (예: "123456-1234567")
     * @param withHyphen 결과에 하이픈을 포함할지 여부 (기본값: false)
     * @return 마스킹된 주민등록번호 (withHyphen=true일 때 "123456-1******", false일 때 "1234561******")
     * @throws IllegalArgumentException 입력값이 올바른 주민등록번호 형식이 아닌 경우
     */
    fun maskSSN(ssn: String?, withHyphen: Boolean = false): String {
        require(!ssn.isNullOrBlank()) { "주민등록번호는 null이거나 빈 값일 수 없습니다." }

        require(PatternValidator.isValidSSN(ssn)) { "올바른 주민등록번호 형식이 아닙니다." }
        
        val numbers = extractNumbers(ssn)
        val prefix = numbers.substring(0, 6)
        val suffix = numbers.substring(6)
        val maskedSuffix = "${suffix.first()}${maskChars(6)}"
        
        return if (withHyphen) {
            "$prefix-$maskedSuffix"
        } else {
            "$prefix$maskedSuffix"
        }
    }

    private fun extractNumbers(input: String?): String {
        return input?.replace(Regex(Constants.NUMERIC_ONLY_PATTERN), "") ?: ""
    }

    private fun maskChars(count: Int): String {
        return Constants.MASK_CHAR.toString().repeat(count)
    }

    /**
     * 휴대폰번호를 마스킹합니다.
     * 
     * 휴대폰번호의 마지막 4자리를 '*'로 대체합니다.
     * 두가지 형식(010-1234-5678, 01012345678 등)을 지원합니다.
     * 
     * @param phone 마스킹할 휴대폰번호
     * @param preserveFormat 원본 형식(하이픈 등)을 유지할지 여부
     * @return 마스킹된 휴대폰번호
     * @throws IllegalArgumentException 입력값이 올바른 전화번호 형식이 아닌 경우
     */
    fun maskPhone(phone: String?, preserveFormat: Boolean = false): String {
        require(!phone.isNullOrBlank()) { "전화번호는 null이거나 빈 값일 수 없습니다." }

        require(PatternValidator.isValidPhoneNumber(phone)) { "올바른 전화번호 형식이 아닙니다." }
        
        return if (preserveFormat) {
            maskPhoneWithFormat(phone)
        } else {
            maskPhoneWithoutFormat(phone)
        }
    }

    private fun maskPhoneWithFormat(phone: String): String {
        val pattern = Regex(Constants.PHONE_PATTERN)
        return pattern.replace(phone) { matchResult ->
            val prefix = matchResult.groupValues[1]
            prefix + Constants.FOUR_DIGIT_MASK
        }
    }

    private fun maskPhoneWithoutFormat(phone: String): String {
        val cleanPhone = extractNumbers(phone)
        val cutIndex = cleanPhone.length - Constants.PHONE_LAST_DIGITS
        val prefix = cleanPhone.substring(0, cutIndex)
        return prefix + Constants.FOUR_DIGIT_MASK
    }

    /**
     * 이름을 마스킹합니다.
     * 
     * 기본적으로 이름의 첫 글자만 표시하고 나머지는 '*'로 대체합니다.
     * 다양한 마스킹 전략을 지원합니다.
     * 
     * @param name 마스킹할 이름
     * @param strategy 적용할 마스킹 전략
     * @return 마스킹된 이름
     * @throws IllegalArgumentException 입력값이 null이거나 빈 값인 경우
     */
    fun maskName(name: String?, strategy: MaskingStrategy = MaskingStrategy.SHOW_FIRST_ONLY): String {
        require(!name.isNullOrBlank()) { "이름은 null이거나 빈 값일 수 없습니다." }
        
        if (name.length <= 1) return name
        if (name.length == 2 && (strategy == MaskingStrategy.SHOW_FIRST_LAST || strategy == MaskingStrategy.MASK_MIDDLE)) {
            return name
        }
        
        return when (strategy) {
            MaskingStrategy.SHOW_FIRST_ONLY -> maskNameShowFirstOnly(name)
            MaskingStrategy.SHOW_LAST_ONLY -> maskNameShowLastOnly(name)
            MaskingStrategy.SHOW_FIRST_LAST -> maskNameShowFirstLast(name)
            MaskingStrategy.MASK_LAST_N -> maskNameLastN(name)
            MaskingStrategy.MASK_MIDDLE -> maskNameMiddle(name)
        }
    }

    private fun maskNameShowFirstOnly(name: String): String = 
        "${name.first()}${maskChars(name.length - 1)}"

    private fun maskNameShowLastOnly(name: String): String = 
        "${maskChars(name.length - 1)}${name.last()}"

    private fun maskNameShowFirstLast(name: String): String = 
        "${name.first()}${maskChars(name.length - 2)}${name.last()}"

    private fun maskNameLastN(name: String): String {
        val maskCount = (name.length / 2).coerceAtLeast(1)
        return "${name.substring(0, name.length - maskCount)}${maskChars(maskCount)}"
    }

    private fun maskNameMiddle(name: String): String = 
        "${name.first()}${maskChars(name.length - 2)}${name.last()}"

    /**
     * 이메일을 마스킹합니다.
     * 
     * 기본적으로 이메일 아이디의 절반을 '*'로 대체합니다.
     * 
     * @param email 마스킹할 이메일
     * @return 마스킹된 이메일
     * @throws IllegalArgumentException 입력값이 올바른 이메일 형식이 아닌 경우
     */
    fun maskEmail(email: String?): String {
        require(!email.isNullOrBlank()) { "이메일은 null이거나 빈 값일 수 없습니다." }

        require(PatternValidator.isValidEmail(email)) { "올바른 이메일 형식이 아닙니다." }
        
        val parts = email.split("@")
        val name = parts[0]
        val domain = parts[1]
        
        if (name.length <= 2) return email
        
        val visibleLength = name.length / 2
        return "${name.substring(0, visibleLength)}${maskChars(name.length - visibleLength)}@$domain"
    }
    
    /**
     * 카드번호를 마스킹합니다.
     * 
     * 카드번호의 중간 8자리를 '*'로 대체합니다. (예: 1234-****-****-5678)
     * 
     * @param cardNumber 마스킹할 카드번호
     * @param preserveFormat 원본 형식(하이픈 등)을 유지할지 여부
     * @return 마스킹된 카드번호
     * @throws IllegalArgumentException 입력값이 올바른 카드번호 형식이 아닌 경우
     */
    fun maskCardNumber(cardNumber: String?, preserveFormat: Boolean = true): String {
        require(!cardNumber.isNullOrBlank()) { "카드번호는 null이거나 빈 값일 수 없습니다." }

        require(PatternValidator.isValidCardNumber(cardNumber)) { "올바른 카드번호 형식이 아닙니다." }
        
        val cleanNumber = extractNumbers(cardNumber)
        val firstVisible = Constants.CARD_FIRST_VISIBLE
        val lastVisible = Constants.CARD_LAST_VISIBLE
        
        val prefix = cleanNumber.substring(0, firstVisible)
        val suffix = cleanNumber.substring(cleanNumber.length - lastVisible)
        val middleLength = cleanNumber.length - firstVisible - lastVisible
        
        // 하이픈 없이 마스킹
        if (!preserveFormat) {
            return "$prefix${maskChars(middleLength)}$suffix"
        }
        
        // 하이픈 포맷 적용 (항상 표준 포맷)
        return "$prefix-${Constants.FOUR_DIGIT_MASK}-${Constants.FOUR_DIGIT_MASK}-$suffix"
    }
}
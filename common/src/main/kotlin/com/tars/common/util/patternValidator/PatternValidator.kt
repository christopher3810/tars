package com.tars.common.util.patternValidator

/**
 * 다양한 형식의 데이터 유효성을 검증하는 유틸리티 클래스입니다.
 *
 * 이 클래스는 이메일, 전화번호, 주민등록번호, 비밀번호, 카드번호 등의
 * 유효성을 검증하는 메서드를 제공합니다.
 */
object PatternValidator {
    // 정규식 패턴 상수 - 필요에 따라 외부에서 접근 가능하도록 const val로 선언
    /**
     * 이메일 주소 검증을 위한 정규식 패턴
     * RFC 5322 기반의 간소화된 패턴
     * 
     * 이 패턴은 다음과 같은 이메일 형식을 허용합니다:
     * - 로컬 파트: 영문자, 숫자, 특수문자(._%+-)
     * - 도메인: 영문자, 숫자, 하이픈(-)
     * - 최상위 도메인: 1자 이상의 영문자
     * - 짧은 도메인 형식도 허용 (예: a@b.c, a@b.c.d)
     */
    const val EMAIL_PATTERN_STRING = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9]{1,}$"
    private val EMAIL_PATTERN = EMAIL_PATTERN_STRING.toRegex()

    /**
     * 전화번호 검증을 위한 정규식 패턴
     * 01X로 시작하는 한국 휴대폰 번호 형식 (하이픈, 공백, 점 등 구분자 허용)
     */
    const val PHONE_PATTERN_STRING = "^01[0-9][-. ]?\\d{3,4}[-. ]?\\d{4}$"
    private val PHONE_PATTERN = PHONE_PATTERN_STRING.toRegex()

    /**
     * 주민등록번호 검증을 위한 정규식 패턴
     * XXXXXX-XXXXXXX 형식, 뒷자리 첫 번째 숫자는 1-4 범위
     * 하이픈은 선택적으로 포함 가능
     */
    const val SSN_PATTERN_STRING = "^\\d{6}[-]?[1-4]\\d{6}$"
    private val SSN_PATTERN = SSN_PATTERN_STRING.toRegex()

    /**
     * 카드번호 검증을 위한 정규식 패턴
     * 12-19자리 숫자, 선택적으로 4자리마다 하이픈 또는 공백으로 구분 가능
     */
    const val CARD_PATTERN_STRING = "^(\\d{4}[-\\s]?){3,4}\\d{1,4}$"
    private val CARD_PATTERN = CARD_PATTERN_STRING.toRegex()

    /**
     * 이메일 주소의 유효성을 검증합니다.
     *
     * 유효한 이메일 형식:
     * - 로컬 파트: 영문자, 숫자, 특수문자(._%+-)
     * - 도메인: 영문자, 숫자, 하이픈(-)
     * - 최상위 도메인: 1자 이상의 영문자
     * - 도메인 레벨: 최대 3레벨까지 허용 (예: a@b.c, a@b.c.d)
     *
     * 참고: 이 검증 로직은 RFC 5322 표준의 일부만 구현하며, 실용적인 이메일 주소 형식만 허용합니다.
     * IP 주소 기반 도메인(예: user@[192.168.0.1])이나 인용된 로컬 파트(예: "user name"@example.com)는 지원하지 않습니다.
     *
     * @param email 검증할 이메일 주소
     * @return 유효한 이메일이면 true, 그렇지 않으면 false
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        
        // 기본 정규식 패턴 검증
        if (!email.matches(EMAIL_PATTERN)) return false
        
        // 도메인 레벨 검증 (최대 3레벨까지만 허용)
        val domainPart = email.substringAfter('@')
        val domainLevels = domainPart.split('.').size
        
        return domainLevels <= 3
    }

    /**
     * 전화번호의 유효성을 검증합니다.
     *
     * 유효한 전화번호 형식:
     * - 01X로 시작하는 한국 휴대폰 번호
     * - 중간에 하이픈(-), 공백, 점(.) 등의 구분자 허용
     * - 총 10-11자리 숫자
     *
     * @param phoneNumber 검증할 전화번호
     * @return 유효한 전화번호이면 true, 그렇지 않으면 false
     */
    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrBlank()) return false
        if (!phoneNumber.matches(PHONE_PATTERN)) return false

        val digits = phoneNumber.replace(Regex("[^0-9]"), "")
        return digits.length in 10..11 && digits.startsWith("01")
    }

    /**
     * 주민등록번호의 유효성을 검증합니다.
     *
     * 유효한 주민등록번호 형식:
     * - XXXXXX-XXXXXXX 형식 (하이픈 포함) 또는 XXXXXXXXXXXXX 형식 (하이픈 미포함)
     * - 앞 6자리: 생년월일
     * - 뒷 7자리: 첫 번째 숫자는 1-4 범위
     * - 기본적인 형식 검증만 수행 (실제 유효한 생년월일인지, 체크섬이 맞는지는 검증하지 않음)
     *
     * @param ssn 검증할 주민등록번호
     * @return 유효한 주민등록번호 형식이면 true, 그렇지 않으면 false
     */
    fun isValidSSN(ssn: String?): Boolean {
        if (ssn.isNullOrBlank()) return false
        
        // 정규식 패턴 검증
        if (!ssn.matches(SSN_PATTERN)) return false
        
        // 숫자만 추출
        val digitsOnly = ssn.replace("-", "")
        
        // 길이 검증 (13자리)
        if (digitsOnly.length != 13) return false
        
        // 앞 6자리와 뒤 7자리 분리
        val front = digitsOnly.substring(0, 6)
        val back = digitsOnly.substring(6)
        
        // 뒷자리 첫 번째 숫자가 1-4 범위인지 검증
        return back[0] in '1'..'4'
    }

    /**
     * 비밀번호의 유효성을 검증합니다.
     *
     * 유효한 비밀번호 조건:
     * - 최소 8자 이상
     * - 최소 1개 이상의 숫자 포함
     * - 최소 1개 이상의 문자 포함
     *
     * @param password 검증할 비밀번호
     * @return 유효한 비밀번호이면 true, 그렇지 않으면 false
     */
    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false

        return password.length >= 8 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() }
    }

    /**
     * 카드번호의 유효성을 검증합니다.
     *
     * 유효한 카드번호 형식:
     * - 12-19자리의 숫자
     * - 선택적으로 4자리마다 하이픈(-) 또는 공백으로 구분 가능
     *
     * @param cardNumber 검증할 카드번호
     * @return 유효한 카드번호 형식이면 true, 그렇지 않으면 false
     */
    fun isValidCardNumber(cardNumber: String?): Boolean {
        if (cardNumber.isNullOrBlank()) return false

        val digitsOnly = cardNumber.replace(Regex("[^0-9]"), "")
        return digitsOnly.length in 12..19
    }

    /**
     * Luhn 알고리즘을 사용하여 카드번호의 유효성을 검증합니다.
     * 이 알고리즘은 대부분의 신용카드 번호에 적용되는 체크섬 검증 방식입니다.
     *
     * 알고리즘 단계:
     * 1. 오른쪽부터 왼쪽으로 짝수 위치의 숫자를 2배로 만듭니다.
     * 2. 2배로 만든 숫자가 9보다 크면 9를 뺍니다(또는 각 자릿수를 더합니다).
     * 3. 모든 숫자를 더합니다.
     * 4. 합계가 10으로 나누어 떨어지면 유효한 번호입니다.
     *
     * @param cardNumber 검증할 카드번호
     * @return Luhn 알고리즘을 통과하면 true, 그렇지 않으면 false
     */
    fun isValidCardNumberWithLuhn(cardNumber: String?): Boolean {
        if (cardNumber.isNullOrBlank()) return false

        // 숫자만 추출
        val digitsOnly = cardNumber.replace(Regex("[^0-9]"), "")

        // 카드번호 길이 검증 (12-19자리)
        if (digitsOnly.length !in 12..19) return false

        // Luhn 알고리즘 적용
        val sum = calculateLuhnSum(digitsOnly)

        // 체크섬이 10으로 나누어 떨어지면 유효
        return sum % 10 == 0
    }

    /**
     * Luhn 알고리즘에 따라 체크섬을 계산합니다.
     *
     * @param digitsOnly 숫자만 포함된 문자열
     * @return 계산된 체크섬
     */
    private fun calculateLuhnSum(digitsOnly: String): Int {
        // 각 자리 숫자를 배열로 변환
        val digits = digitsOnly.map { it.toString().toInt() }

        // 오른쪽에서 왼쪽으로 처리하기 위해 배열 뒤집기
        val reversedDigits = digits.reversed()

        // 체크섬 계산
        var sum = 0

        for (i in reversedDigits.indices) {
            val digit = reversedDigits[i]

            // 짝수 위치(인덱스는 0부터 시작하므로 홀수 인덱스)의 숫자는 2배로
            if (i % 2 == 1) {
                val doubled = digit * 2
                // 2배한 값이 9보다 크면 9를 뺌 (이는 각 자릿수를 더하는 것과 동일)
                sum += if (doubled > 9) doubled - 9 else doubled
            } else {
                // 홀수 위치의 숫자는 그대로 더함
                sum += digit
            }
        }

        return sum
    }

    /**
     * 패턴 유형에 따라 입력값의 유효성을 검증합니다.
     * 확장성을 고려한 범용 검증 메서드입니다.
     *
     * @param input 검증할 입력값
     * @param type 검증할 패턴 유형
     * @return 유효한 형식이면 true, 그렇지 않으면 false
     */
    fun isValid(input: String?, type: PatternType): Boolean = when(type) {
        PatternType.EMAIL -> isValidEmail(input)
        PatternType.PHONE -> isValidPhoneNumber(input)
        PatternType.SSN -> isValidSSN(input)
        PatternType.PASSWORD -> isValidPassword(input)
        PatternType.CARD -> isValidCardNumber(input)
        PatternType.CARD_WITH_LUHN -> isValidCardNumberWithLuhn(input)
    }

    /**
     * 검증할 패턴 유형을 정의하는 열거형
     */
    enum class PatternType {
        EMAIL,
        PHONE,
        SSN,
        PASSWORD,
        CARD,
        CARD_WITH_LUHN
    }
} 
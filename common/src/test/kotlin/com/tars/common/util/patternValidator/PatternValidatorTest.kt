package com.tars.common.util.patternValidator

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.assertions.withClue

class PatternValidatorTest : DescribeSpec({

    val sut = PatternValidator
    
    describe("PatternValidator는") {
        
        context("이메일 주소를 검증할 때") {
            it("유효한 이메일 주소는 true를 반환해야 한다") {
                forAll(
                    row("test@example.com"),
                    row("user.name@domain.co.kr"),
                    row("user+tag@example.org"),
                    row("123@456.com"),
                    row("a@b.c"),
                    row("a@b.c.d")
                ) { email ->
                    withClue("이메일 '$email'은 유효해야 합니다") {
                        sut.isValidEmail(email) shouldBe true
                    }
                }
            }
            
            it("유효하지 않은 이메일 주소는 false를 반환해야 한다") {
                forAll(
                    row(""),
                    row("plaintext"),
                    row("@domain.com"),
                    row("user@"),
                    row("user@.com"),
                    row("user@domain."),
                    row("user name@domain.com"),
                    row("a@b.c.d.e"), // 도메인 레벨이 너무 많음
                    row("a@b.c.d.e.f"), // 도메인 레벨이 너무 많음
                    row(null)
                ) { email ->
                    withClue("이메일 '$email'은 유효하지 않아야 합니다") {
                        sut.isValidEmail(email) shouldBe false
                    }
                }
            }
            
            it("도메인 레벨 제한을 준수해야 한다") {
                withClue("2레벨 도메인 이메일은 유효해야 합니다") {
                    sut.isValidEmail("user@example.com") shouldBe true
                }
                
                withClue("3레벨 도메인 이메일은 유효해야 합니다") {
                    sut.isValidEmail("user@sub.example.com") shouldBe true
                }
                
                withClue("4레벨 도메인 이메일은 유효하지 않아야 합니다") {
                    sut.isValidEmail("user@sub.sub.example.com") shouldBe false
                }
                
                withClue("5레벨 도메인 이메일은 유효하지 않아야 합니다") {
                    sut.isValidEmail("user@a.b.c.d.e") shouldBe false
                }
            }
            
            it("PatternType.EMAIL로 검증해도 동일한 결과를 반환해야 한다") {
                val validEmail = "test@example.com"
                val invalidEmail = "invalid"
                
                withClue("유효한 이메일 '$validEmail'은 PatternType.EMAIL로 검증해도 true를 반환해야 합니다") {
                    sut.isValid(validEmail, PatternValidator.PatternType.EMAIL) shouldBe true
                }
                
                withClue("유효하지 않은 이메일 '$invalidEmail'은 PatternType.EMAIL로 검증해도 false를 반환해야 합니다") {
                    sut.isValid(invalidEmail, PatternValidator.PatternType.EMAIL) shouldBe false
                }
            }
        }
        
        context("전화번호를 검증할 때") {
            it("유효한 전화번호는 true를 반환해야 한다") {
                forAll(
                    row("01012345678"),
                    row("010-1234-5678"),
                    row("010.1234.5678"),
                    row("010 1234 5678"),
                    row("01112345678"),
                    row("011-123-4567")
                ) { phone ->
                    withClue("전화번호 '$phone'은 유효해야 합니다") {
                        sut.isValidPhoneNumber(phone) shouldBe true
                    }
                }
            }
            
            it("유효하지 않은 전화번호는 false를 반환해야 한다") {
                forAll(
                    row(""),
                    row("12345678"),
                    row("02-123-4567"), // 지역번호는 지원하지 않음
                    row("010-123-456"), // 자릿수 부족
                    row("010-123-45678"), // 자릿수 초과
                    row("abc-defg-hijk"),
                    row(null)
                ) { phone ->
                    withClue("전화번호 '$phone'은 유효하지 않아야 합니다") {
                        sut.isValidPhoneNumber(phone) shouldBe false
                    }
                }
            }
            
            it("PatternType.PHONE으로 검증해도 동일한 결과를 반환해야 한다") {
                val validPhone = "010-1234-5678"
                val invalidPhone = "02-123-4567"
                
                withClue("유효한 전화번호 '$validPhone'은 PatternType.PHONE으로 검증해도 true를 반환해야 합니다") {
                    sut.isValid(validPhone, PatternValidator.PatternType.PHONE) shouldBe true
                }
                
                withClue("유효하지 않은 전화번호 '$invalidPhone'은 PatternType.PHONE으로 검증해도 false를 반환해야 합니다") {
                    sut.isValid(invalidPhone, PatternValidator.PatternType.PHONE) shouldBe false
                }
            }
        }
        
        context("주민등록번호를 검증할 때") {
            it("유효한 주민등록번호 형식은 true를 반환해야 한다") {
                forAll(
                    row("123456-1234567"),
                    row("1234561234567"),
                    row("123456-2234567"),
                    row("123456-3234567"),
                    row("123456-4234567")
                ) { ssn ->
                    withClue("주민등록번호 '$ssn'은 유효해야 합니다") {
                        sut.isValidSSN(ssn) shouldBe true
                    }
                }
            }
            
            it("유효하지 않은 주민등록번호 형식은 false를 반환해야 한다") {
                forAll(
                    row(""),
                    row("12345-1234567"), // 앞자리 부족
                    row("1234567-123456"), // 뒷자리 부족
                    row("123456-5234567"), // 성별 식별자 범위 초과
                    row("123456-0234567"), // 성별 식별자 범위 미만
                    row("123456=1234567"), // 잘못된 구분자
                    row("abcdef-1234567"), // 숫자가 아님
                    row(null)
                ) { ssn ->
                    withClue("주민등록번호 '$ssn'은 유효하지 않아야 합니다") {
                        sut.isValidSSN(ssn) shouldBe false
                    }
                }
            }
            
            it("PatternType.SSN으로 검증해도 동일한 결과를 반환해야 한다") {
                val validSSN = "123456-1234567"
                val invalidSSN = "12345-1234567"
                
                withClue("유효한 주민등록번호 '$validSSN'은 PatternType.SSN으로 검증해도 true를 반환해야 합니다") {
                    sut.isValid(validSSN, PatternValidator.PatternType.SSN) shouldBe true
                }
                
                withClue("유효하지 않은 주민등록번호 '$invalidSSN'은 PatternType.SSN으로 검증해도 false를 반환해야 합니다") {
                    sut.isValid(invalidSSN, PatternValidator.PatternType.SSN) shouldBe false
                }
            }
        }
        
        context("비밀번호를 검증할 때") {
            it("유효한 비밀번호는 true를 반환해야 한다") {
                forAll(
                    row("password123"),
                    row("Pass1234"),
                    row("1234abcd"),
                    row("p1a2s3s4w5o6r7d8"),
                    row("P@ssw0rd")
                ) { password ->
                    withClue("비밀번호 '$password'는 유효해야 합니다") {
                        sut.isValidPassword(password) shouldBe true
                    }
                }
            }
            
            it("유효하지 않은 비밀번호는 false를 반환해야 한다") {
                forAll(
                    row(""),
                    row("pass"), // 8자 미만
                    row("password"), // 숫자 없음
                    row("12345678"), // 문자 없음
                    row(null)
                ) { password ->
                    withClue("비밀번호 '$password'는 유효하지 않아야 합니다") {
                        sut.isValidPassword(password) shouldBe false
                    }
                }
            }
            
            it("PatternType.PASSWORD로 검증해도 동일한 결과를 반환해야 한다") {
                val validPassword = "password123"
                val invalidPassword = "pass"
                
                withClue("유효한 비밀번호 '$validPassword'는 PatternType.PASSWORD로 검증해도 true를 반환해야 합니다") {
                    sut.isValid(validPassword, PatternValidator.PatternType.PASSWORD) shouldBe true
                }
                
                withClue("유효하지 않은 비밀번호 '$invalidPassword'는 PatternType.PASSWORD로 검증해도 false를 반환해야 합니다") {
                    sut.isValid(invalidPassword, PatternValidator.PatternType.PASSWORD) shouldBe false
                }
            }
        }
        
        context("카드번호를 검증할 때") {
            it("유효한 카드번호 형식은 true를 반환해야 한다") {
                forAll(
                    row("1234567890123456"),
                    row("1234-5678-9012-3456"),
                    row("1234 5678 9012 3456"),
                    row("123456789012"),
                    row("1234567890123456789") // 19자리
                ) { cardNumber ->
                    withClue("카드번호 '$cardNumber'는 유효해야 합니다") {
                        sut.isValidCardNumber(cardNumber) shouldBe true
                    }
                }
            }
            
            it("유효하지 않은 카드번호 형식은 false를 반환해야 한다") {
                forAll(
                    row(""),
                    row("12345678901"), // 11자리 (최소 12자리 필요)
                    row("12345678901234567890"), // 20자리 (최대 19자리)
                    row("abcd-efgh-ijkl-mnop"), // 숫자가 아님
                    row(null)
                ) { cardNumber ->
                    withClue("카드번호 '$cardNumber'는 유효하지 않아야 합니다") {
                        sut.isValidCardNumber(cardNumber) shouldBe false
                    }
                }
            }
            
            it("PatternType.CARD로 검증해도 동일한 결과를 반환해야 한다") {
                val validCardNumber = "1234-5678-9012-3456"
                val invalidCardNumber = "12345"
                
                withClue("유효한 카드번호 '$validCardNumber'는 PatternType.CARD로 검증해도 true를 반환해야 합니다") {
                    sut.isValid(validCardNumber, PatternValidator.PatternType.CARD) shouldBe true
                }
                
                withClue("유효하지 않은 카드번호 '$invalidCardNumber'는 PatternType.CARD로 검증해도 false를 반환해야 합니다") {
                    sut.isValid(invalidCardNumber, PatternValidator.PatternType.CARD) shouldBe false
                }
            }
        }
        
        context("Luhn 알고리즘으로 카드번호를 검증할 때") {
            it("Luhn 알고리즘을 통과하는 카드번호는 true를 반환해야 한다") {
                forAll(
                    row("4532015112830366"), // Visa
                    row("5424000000000015"), // Mastercard
                    row("378282246310005"), // American Express
                    row("6011000000000012"), // Discover
                    row("3530111333300000") // JCB
                ) { cardNumber ->
                    withClue("카드번호 '$cardNumber'는 Luhn 알고리즘을 통과해야 합니다") {
                        sut.isValidCardNumberWithLuhn(cardNumber) shouldBe true
                    }
                }
            }
            
            it("Luhn 알고리즘을 통과하지 못하는 카드번호는 false를 반환해야 한다") {
                forAll(
                    row(""),
                    row("1234567890123456"), // 형식은 맞지만 Luhn 알고리즘 실패
                    row("4532015112830367"), // 마지막 숫자 변경
                    row("abcd-efgh-ijkl-mnop"), // 숫자가 아님
                    row(null)
                ) { cardNumber ->
                    withClue("카드번호 '$cardNumber'는 Luhn 알고리즘을 통과하지 못해야 합니다") {
                        sut.isValidCardNumberWithLuhn(cardNumber) shouldBe false
                    }
                }
            }
            
            it("PatternType.CARD_WITH_LUHN으로 검증해도 동일한 결과를 반환해야 한다") {
                val validCardNumber = "4532015112830366" // Visa
                val invalidCardNumber = "1234567890123456"
                
                withClue("유효한 카드번호 '$validCardNumber'는 PatternType.CARD_WITH_LUHN으로 검증해도 true를 반환해야 합니다") {
                    sut.isValid(validCardNumber, PatternValidator.PatternType.CARD_WITH_LUHN) shouldBe true
                }
                
                withClue("유효하지 않은 카드번호 '$invalidCardNumber'는 PatternType.CARD_WITH_LUHN으로 검증해도 false를 반환해야 합니다") {
                    sut.isValid(invalidCardNumber, PatternValidator.PatternType.CARD_WITH_LUHN) shouldBe false
                }
            }
        }
        
        context("PatternType 열거형을 사용하여 검증할 때") {
            it("각 패턴 유형에 맞는 검증 메서드를 호출해야 한다") {
                val validInputs = mapOf(
                    PatternValidator.PatternType.EMAIL to "test@example.com",
                    PatternValidator.PatternType.PHONE to "010-1234-5678",
                    PatternValidator.PatternType.SSN to "123456-1234567",
                    PatternValidator.PatternType.PASSWORD to "password123",
                    PatternValidator.PatternType.CARD to "1234-5678-9012-3456",
                    PatternValidator.PatternType.CARD_WITH_LUHN to "4532015112830366"
                )
                
                validInputs.forEach { (type, input) ->
                    withClue("입력값 '$input'은 $type 유형으로 검증 시 유효해야 합니다") {
                        sut.isValid(input, type) shouldBe true
                    }
                }
            }
        }
    }
}) 
package com.tars.common.util.masking

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.assertions.withClue

/**
 * MaskingUtil 클래스에 대한 테스트
 */
class MaskingUtilTest : DescribeSpec({
    
    // SUT(System Under Test) 정의
    val sut = MaskingUtil
    
    describe("MaskingUtil은") {
        
        context("주민등록번호를 마스킹할 때") {
            it("뒷자리 첫 번째 숫자를 제외하고 마스킹해야 한다") {
                val ssn = "123456-1234567"
                val expected = "123456-1******"
                
                withClue("주민등록번호 '$ssn'은 '$expected'로 마스킹되어야 합니다") {
                    sut.maskSSN(ssn, true) shouldBe expected
                }
            }
            
            it("하이픈 포함 여부에 따라 결과가 달라져야 한다") {
                val ssn = "123456-1234567"
                
                withClue("하이픈 포함 옵션이 true일 때 '$ssn'은 '123456-1******'로 마스킹되어야 합니다") {
                    sut.maskSSN(ssn, true) shouldBe "123456-1******"
                }
                
                withClue("하이픈 포함 옵션이 false일 때 '$ssn'은 '1234561******'로 마스킹되어야 합니다") {
                    sut.maskSSN(ssn, false) shouldBe "1234561******"
                }
            }
            
            it("하이픈이 없는 주민등록번호도 처리해야 한다") {
                val ssn = "1234561234567"
                
                withClue("하이픈이 없는 주민등록번호 '$ssn'도 하이픈 포함 옵션에 따라 적절히 마스킹되어야 합니다") {
                    sut.maskSSN(ssn, true) shouldBe "123456-1******"
                    sut.maskSSN(ssn, false) shouldBe "1234561******"
                }
            }
            
            it("유효하지 않은 주민등록번호는 예외를 발생시켜야 한다") {
                val invalidSSN = "12345-1234567" // 앞자리 부족
                
                withClue("유효하지 않은 주민등록번호 '$invalidSSN'은 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskSSN(invalidSSN)
                    }
                }
            }
            
            it("null이나 빈 문자열은 예외를 발생시켜야 한다") {
                withClue("null 주민등록번호는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskSSN(null)
                    }
                }
                
                withClue("빈 문자열 주민등록번호는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskSSN("")
                    }
                }
            }
        }
        
        context("전화번호를 마스킹할 때") {
            it("마지막 4자리를 마스킹해야 한다") {
                val phone = "010-1234-5678"
                val expected = "010-1234-****"
                
                withClue("전화번호 '$phone'은 '$expected'로 마스킹되어야 합니다") {
                    sut.maskPhone(phone, true) shouldBe expected
                }
            }
            
            it("형식 유지 여부에 따라 결과가 달라져야 한다") {
                val phone = "010-1234-5678"
                
                withClue("형식 유지 옵션이 true일 때 '$phone'은 '010-1234-****'로 마스킹되어야 합니다") {
                    sut.maskPhone(phone, true) shouldBe "010-1234-****"
                }
                
                withClue("형식 유지 옵션이 false일 때 '$phone'은 '0101234****'로 마스킹되어야 합니다") {
                    sut.maskPhone(phone, false) shouldBe "0101234****"
                }
            }

            it("점(.)이나 공백이 포함된 전화번호도 마스킹되어야 한다") {
                forAll(
                    row("010.1234.5678", "010.1234.****"),
                    row("010 1234 5678", "010 1234 ****")
                ) { phone, expected ->
                    withClue("전화번호 '$phone'은 형식 유지 옵션이 true일 때 '$expected'로 마스킹되어야 합니다") {
                        sut.maskPhone(phone, true) shouldBe expected
                    }
                }
                
                forAll(
                    row("010.1234.5678", "0101234****"),
                    row("010 1234 5678", "0101234****")
                ) { phone, expected ->
                    withClue("전화번호 '$phone'은 형식 유지 옵션이 false일 때 '$expected'로 마스킹되어야 합니다") {
                        sut.maskPhone(phone, false) shouldBe expected
                    }
                }
            }

            it("유효하지 않은 전화번호는 예외를 발생시켜야 한다") {
                forAll(
                    row("02-123-4567") // 지역번호는 지원하지 않음
                ) { invalidPhone ->
                    withClue("유효하지 않은 전화번호 '$invalidPhone'은 예외를 발생시켜야 합니다") {
                        shouldThrow<IllegalArgumentException> {
                            sut.maskPhone(invalidPhone)
                        }
                    }
                }
            }
            
            it("null이나 빈 문자열은 예외를 발생시켜야 한다") {
                withClue("null 전화번호는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskPhone(null)
                    }
                }
                
                withClue("빈 문자열 전화번호는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskPhone("")
                    }
                }
            }
        }
        
        context("이름을 마스킹할 때") {
            it("기본 전략(SHOW_FIRST_ONLY)은 첫 글자만 표시해야 한다") {
                val name = "홍길동"
                val expected = "홍**"
                
                withClue("이름 '$name'은 기본 전략으로 '$expected'로 마스킹되어야 합니다") {
                    sut.maskName(name) shouldBe expected
                }
            }
            
            it("다양한 마스킹 전략에 따라 결과가 달라져야 한다") {
                val name = "홍길동"
                
                forAll(
                    row(MaskingUtil.MaskingStrategy.SHOW_LAST_ONLY, "**동", "마지막 글자만 표시"),
                    row(MaskingUtil.MaskingStrategy.SHOW_FIRST_LAST, "홍*동", "첫 글자와 마지막 글자만 표시"),
                    row(MaskingUtil.MaskingStrategy.MASK_LAST_N, "홍길*", "마지막 N개 문자 마스킹"),
                    row(MaskingUtil.MaskingStrategy.MASK_MIDDLE, "홍*동", "중간 부분 마스킹")
                ) { strategy, expected, description ->
                    withClue("이름 '$name'은 $description 전략으로 '$expected'로 마스킹되어야 합니다") {
                        sut.maskName(name, strategy) shouldBe expected
                    }
                }
            }
            
            it("2글자 이름은 SHOW_FIRST_LAST 또는 MASK_MIDDLE 전략에서 마스킹하지 않아야 한다") {
                val name = "길동"
                
                withClue("2글자 이름 '$name'은 SHOW_FIRST_LAST 전략에서 마스킹하지 않아야 합니다") {
                    sut.maskName(name, MaskingUtil.MaskingStrategy.SHOW_FIRST_LAST) shouldBe "길동"
                }
                
                withClue("2글자 이름 '$name'은 MASK_MIDDLE 전략에서 마스킹하지 않아야 합니다") {
                    sut.maskName(name, MaskingUtil.MaskingStrategy.MASK_MIDDLE) shouldBe "길동"
                }
            }
            
            it("1글자 이름은 마스킹하지 않아야 한다") {
                val name = "홍"
                
                withClue("1글자 이름 '$name'은 마스킹하지 않아야 합니다") {
                    sut.maskName(name) shouldBe "홍"
                }
            }
            
            it("null이나 빈 문자열은 예외를 발생시켜야 한다") {
                withClue("null 이름은 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskName(null)
                    }
                }
                
                withClue("빈 문자열 이름은 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskName("")
                    }
                }
            }
        }
        
        context("이메일을 마스킹할 때") {
            it("아이디의 절반을 마스킹해야 한다") {
                val email = "example@domain.com"
                val expected = "exa****@domain.com"
                
                withClue("이메일 '$email'은 '$expected'로 마스킹되어야 합니다") {
                    sut.maskEmail(email) shouldBe expected
                }
            }
            
            it("아이디가 2글자 이하면 마스킹하지 않아야 한다") {
                val email = "ex@domain.com"
                
                withClue("아이디가 2글자인 이메일 '$email'은 마스킹하지 않아야 합니다") {
                    sut.maskEmail(email) shouldBe "ex@domain.com"
                }
            }
            
            it("유효하지 않은 이메일은 예외를 발생시켜야 한다") {
                val invalidEmail = "invalid"
                
                withClue("유효하지 않은 이메일 '$invalidEmail'은 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskEmail(invalidEmail)
                    }
                }
            }
            
            it("null이나 빈 문자열은 예외를 발생시켜야 한다") {
                withClue("null 이메일은 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskEmail(null)
                    }
                }
                
                withClue("빈 문자열 이메일은 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskEmail("")
                    }
                }
            }
        }
        
        context("카드번호를 마스킹할 때") {
            it("중간 8자리를 마스킹해야 한다") {
                val cardNumber = "1234-5678-9012-3456"
                val expected = "1234-****-****-3456"
                
                withClue("카드번호 '$cardNumber'는 '$expected'로 마스킹되어야 합니다") {
                    sut.maskCardNumber(cardNumber) shouldBe expected
                }
            }
            
            it("형식 유지 여부에 따라 결과가 달라져야 한다") {
                val cardNumber = "1234-5678-9012-3456"
                
                withClue("형식 유지 옵션이 true일 때 '$cardNumber'는 '1234-****-****-3456'로 마스킹되어야 합니다") {
                    sut.maskCardNumber(cardNumber, true) shouldBe "1234-****-****-3456"
                }
                
                withClue("형식 유지 옵션이 false일 때 '$cardNumber'는 '1234********3456'로 마스킹되어야 합니다") {
                    sut.maskCardNumber(cardNumber, false) shouldBe "1234********3456"
                }
            }
            
            it("하이픈이 없는 카드번호도 처리해야 한다") {
                val cardNumber = "1234567890123456"
                
                withClue("하이픈이 없는 카드번호 '$cardNumber'도 형식 유지 옵션에 따라 적절히 마스킹되어야 합니다") {
                    sut.maskCardNumber(cardNumber, true) shouldBe "1234-****-****-3456"
                    sut.maskCardNumber(cardNumber, false) shouldBe "1234********3456"
                }
            }
            
            it("유효하지 않은 카드번호는 예외를 발생시켜야 한다") {
                val invalidCardNumber = "12345" // 자릿수 부족
                
                withClue("유효하지 않은 카드번호 '$invalidCardNumber'는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskCardNumber(invalidCardNumber)
                    }
                }
            }
            
            it("null이나 빈 문자열은 예외를 발생시켜야 한다") {
                withClue("null 카드번호는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskCardNumber(null)
                    }
                }
                
                withClue("빈 문자열 카드번호는 예외를 발생시켜야 합니다") {
                    shouldThrow<IllegalArgumentException> {
                        sut.maskCardNumber("")
                    }
                }
            }
        }
    }
}) 
package com.tars.common.util

import java.security.SecureRandom
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

/**
 * 10자리 고유 사용자 ID 생성기
 * 
 * 생성 방식:
 * - 현재 시간의 마지막 6자리 (밀리초 단위)
 * - 3자리 랜덤 숫자
 * - 1자리 시퀀스 번호 (0-9 순환)
 */
object UserIdGenerator {
    private val random = SecureRandom()
    private val sequence = AtomicInteger(0)
    
    /**
     * 10자리 고유 사용자 ID를 생성.
     * 
     * @return 10자리 숫자로 구성된 고유 ID
     */
    fun generate(): Long {
        // 현재 시간의 마지막 6자리 (밀리초)
        val timestamp = (Instant.now().toEpochMilli() % 1_000_000)
        
        // 3자리 랜덤 숫자 (100-999)
        val randomPart = 100 + random.nextInt(900)
        
        // 1자리 시퀀스 (0-9 순환)
        val seq = sequence.getAndIncrement() % 10
        
        // 최종 ID 조합: 6자리 타임스탬프 + 3자리 랜덤 + 1자리 시퀀스
        return timestamp * 10_000 + randomPart * 10 + seq
    }
    
    /**
     * 생성된 ID가 10자리인지 검증.
     * 
     * @param id 검증할 ID
     * @return 10자리 ID인 경우 true, 아니면 false
     */
    fun isValid(id: Long): Boolean {
        return id in 1_000_000_000..9_999_999_999
    }
} 
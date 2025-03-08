package com.tars.common.util.idGenerator

import java.time.LocalTime
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import kotlin.random.Random

/**
 * 10자리 숫자 ID 생성기 (lock-free CAS 버전).
 *
 * - daySecond(0..86399) + machineId(1자리) + seq(0..99) + random(0..99).
 * - 여러 스레드/코루틴이 동시에 generate() 호출해도, Atomic CAS로 충돌 없이 seq를 증가.
 * - 초당 100건 넘으면 seq가 겹칠 가능성. (여기선 %100만 처리)
 * - 하루가 바뀌면 daySecond가 0부터 다시 시작 (이전 날짜와 겹칠 우려는 낮지만 완전히 배제 X).
 */
object UserIdGeneratorCas {

    /** 서버마다 다르게 설정하면 충돌 줄어듬 (기본 0) */
    var machineId: Int = run {
        val env = System.getenv("MACHINE_ID") ?: "0"
        abs(env.toIntOrNull() ?: 0) % 10
    }

    // daySecond + sequence 묶은 상태
    private data class State(val daySecond: Int, val seq: Int)

    /**
     * AtomicReference로 lastState 보관.
     * 초기값: daySecond = -1, seq = 0
     */
    private val lastState = AtomicReference(State(-1, 0))

    fun generate(): Long {
        var current: State
        var updated: State

        val nowSec = LocalTime.now().toSecondOfDay() // 0..86399

        while (true) {
            // 1) 현재 상태 읽기
            current = lastState.get()

            // 2) nowSec == current.daySecond면 seq+1, 아니면 seq=0
            val newSeq = if (nowSec == current.daySecond) {
                (current.seq + 1) % 100
            } else {
                0
            }

            updated = State(nowSec, newSeq)

            // 3) CAS로 state 갱신 시도
            if (lastState.compareAndSet(current, updated)) {
                // 성공 시 빠져나감
                break
            }
            // 실패하면 다른 스레드가 먼저 갱신 -> 루프 재시도
        }

        // dayPart: 5자리 (10000..96399)
        val dayPart = nowSec + 10000
        val randVal = Random.nextInt(100) // 2자리
        // updated.seq => 2자리
        val idStr = "%05d%d%02d%02d".format(dayPart, machineId, updated.seq, randVal)
        return idStr.toLong()
    }

    fun isValid(id: Long): Boolean {
        return id in 1_000_000_000..9_999_999_999
    }
}

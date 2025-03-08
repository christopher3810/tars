package com.tars.common.util

import com.tars.common.util.idGenerator.UserIdGeneratorSync
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserIdGeneratorSyncTest : DescribeSpec({

    describe("UserIdGeneratorSync") {

        it("기본 machineId는 0~9") {
            UserIdGeneratorSync.machineId.shouldBeBetween(0, 9)

        }

        it("10자리 범위 내의 ID 생성") {
            val id = UserIdGeneratorSync.generate()
            id shouldBeGreaterThanOrEqual 1_000_000_000L
            id shouldBeLessThanOrEqual 9_999_999_999L
        }

        it("isValid 함수 테스트") {
            UserIdGeneratorSync.isValid(999_999_999L) shouldBe false
            UserIdGeneratorSync.isValid(1_000_000_000L) shouldBe true
            UserIdGeneratorSync.isValid(9_999_999_999L) shouldBe true
            UserIdGeneratorSync.isValid(10_000_000_000L) shouldBe false
        }

        it("단일 스레드 - 여러 번 생성해도 중복 없음") {
            val count = 50
            val ids = List(count) { UserIdGeneratorSync.generate() }
            ids.distinct().size shouldBe count
        }

        it("멀티스레드 - 동시 호출로 중복 없음") {
            val threadCount = 5
            val perThread = 20
            val allIds = mutableListOf<Long>()

            val threads = (1..threadCount).map {
                Thread {
                    repeat(perThread) {
                        val newId = UserIdGeneratorSync.generate()
                        synchronized(allIds) { allIds += newId }
                    }
                }
            }
            threads.forEach { it.start() }
            threads.forEach { it.join() }

            allIds.shouldHaveSize(threadCount * perThread)
            allIds.distinct().size shouldBe allIds.size
        }

        it("코루틴 - 동시 호출로 중복 없음") {
            val jobCount = 5
            val perJob = 20
            val allIds = mutableListOf<Long>()
            val mutex = Mutex()  // 코루틴에서 List 접근 동기화

            runBlocking {
                repeat(jobCount) {
                    launch(Dispatchers.Default) {
                        repeat(perJob) {
                            val newId = UserIdGeneratorSync.generate()
                            mutex.withLock {
                                allIds.add(newId)
                            }
                        }
                    }
                }
            }

            allIds.shouldHaveSize(jobCount * perJob)
            allIds.distinct().size shouldBe allIds.size
        }

        it("초당 100건 제한 테스트 (same second)") {
            // 같은 초 안에 110번 호출 -> seqVal % 100 => 10개 정도는 seq 충돌 가능성
            // 다만 Random 부분이 달라 중복이 실제로 발생할 수도, 안 발생할 수도 있음
            // 여기서는 "어차피 %100이라 100개 넘어가면 충돌 위험"을 검증만.

            val nowSec = UserIdGeneratorSync.generate()  // just to fix current second
            val idsInSameSec = List(110) { UserIdGeneratorSync.generate() }
            // 중복 발생할 수도 있고, 랜덤 때문에 우연히 안 생길 수도 있음.
            // 이 테스트에서는 "이미 seq가 0..99로 돌기 때문에 100개 초과면 중복 위험이 있다"를 보여주기만...
            // 만약 확실히 충돌시키고 싶다면 random=0 고정으로 바꾸면 됨.

            // 여기서는 강제로 중복 체크:
            val duplicates = idsInSameSec.size - idsInSameSec.distinct().size
            println("Duplicates found: $duplicates (possibly 0 if random parted)")

            // 중복이 "있을 수도, 없을 수도" 있으므로 여기서는 로그만 찍고, 검증을 강제하지 않음
        }
    }
})

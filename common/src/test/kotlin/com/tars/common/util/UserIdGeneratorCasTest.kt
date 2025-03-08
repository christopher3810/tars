package com.tars.common.util

import com.tars.common.util.idGenerator.UserIdGeneratorCas
import com.tars.common.util.idGenerator.UserIdGeneratorSync
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserIdGeneratorCasTest : DescribeSpec({

    describe("UserIdGeneratorCas") {

        it("machineId는 0~9") {
            UserIdGeneratorSync.machineId.shouldBeBetween(0, 9)

        }

        it("10자리 범위 내 ID 생성") {
            val id = UserIdGeneratorCas.generate()
            id shouldBeGreaterThanOrEqual 1_000_000_000L
            id shouldBeLessThanOrEqual 9_999_999_999L
        }

        it("isValid 함수") {
            UserIdGeneratorCas.isValid(999_999_999L) shouldBe false
            UserIdGeneratorCas.isValid(1_000_000_000L) shouldBe true
            UserIdGeneratorCas.isValid(9_999_999_999L) shouldBe true
            UserIdGeneratorCas.isValid(10_000_000_000L) shouldBe false
        }

        it("단일 스레드 반복 생성 => 중복 없음") {
            val count = 50
            val ids = List(count) { UserIdGeneratorCas.generate() }
            ids.distinct().size shouldBe count
        }

        it("멀티스레드 동시 호출 => 중복 없음") {
            val threadCount = 5
            val perThread = 20
            val allIds = mutableListOf<Long>()

            val threads = (1..threadCount).map {
                Thread {
                    repeat(perThread) {
                        val newId = UserIdGeneratorCas.generate()
                        synchronized(allIds) { allIds += newId }
                    }
                }
            }
            threads.forEach { it.start() }
            threads.forEach { it.join() }

            allIds.shouldHaveSize(threadCount * perThread)
            allIds.distinct().size shouldBe allIds.size
        }

        it("코루틴 동시 호출 => 중복 없음") {
            val jobCount = 5
            val perJob = 20
            val allIds = mutableListOf<Long>()
            val mutex = Mutex()

            runBlocking {
                repeat(jobCount) {
                    launch(Dispatchers.Default) {
                        repeat(perJob) {
                            val newId = UserIdGeneratorCas.generate()
                            mutex.withLock {
                                allIds += newId
                            }
                        }
                    }
                }
            }

            allIds.shouldHaveSize(jobCount * perJob)
            allIds.distinct().size shouldBe allIds.size
        }

        it("초당 100건 넘어가는 상황") {
            val nowSec = UserIdGeneratorCas.generate()  // warmup
            val idsInSameSec = List(120) { UserIdGeneratorCas.generate() }
            val distinctSize = idsInSameSec.distinct().size
            println("Generated 120 IDs in same second. Distinct = $distinctSize")
            // 시퀀스 %100 이므로 중복 위험 있음, random이 다르면 안 생길 수도 있음
        }
    }
})

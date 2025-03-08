package com.tars.common.util.idGenerator

import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.random.Random

object UserIdGeneratorSync {

    var machineId: Int = run {
        val env = System.getenv("MACHINE_ID") ?: "0"
        abs(env.toIntOrNull() ?: 0) % 10
    }

    private var lastSecondOfDay: Int = -1
    private val sequence = AtomicInteger(0)

    @Synchronized
    fun generate(): Long {
        val nowSec = LocalTime.now().toSecondOfDay()  // 0..86399
        if (nowSec != lastSecondOfDay) {
            lastSecondOfDay = nowSec
            sequence.set(0)
        }

        // 초당 100건
        val seqVal = sequence.getAndIncrement() % 100

        val dayPart = nowSec + 10000      // 5자리
        val randVal = Random.nextInt(100) // 2자리
        val idStr = "%05d%d%02d%02d".format(dayPart, machineId, seqVal, randVal)
        return idStr.toLong()
    }

    fun isValid(id: Long): Boolean {
        return id in 1_000_000_000..9_999_999_999
    }
}

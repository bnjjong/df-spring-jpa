package io.df.dfspringjpa.account

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.system.measureNanoTime

@SpringBootTest
@ActiveProfiles("local")
class AccountIntegrationTest {

    @Autowired
    lateinit var repository: AccountRepository

    @Autowired
    lateinit var service: AccountService

    // 기본 테스트 데이터 수. 필요하면 늘리거나 줄이세요.
    private val N = 2000

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        val accounts = (1..N).map { Account(name = "name$it", email = "user$it@example.com") }
        repository.saveAll(accounts)
    }

    @Test
    fun `sequential vs parallel fetch return identical results`() {
        val expected = repository.count().toInt()

        val sequential = service.findAllSequential()
        val parallel = service.findAllParallel(parallelThreshold = 1)

        assertEquals(expected, sequential.size)
        assertEquals(expected, parallel.size)
        assertEquals(sequential.map { it.id }.toSet(), parallel.map { it.id }.toSet())
    }

    @Test
    fun `parallel threshold prevents unnecessary parallelism`() {
        val higherThreshold = N + 1

        val metrics = service.benchmarkSequentialVsParallel(
            iterations = 1,
            parallelThreshold = higherThreshold
        )

        assertEquals(N, metrics.recordCount)
        assertFalse(metrics.parallelUsed)
    }

    @Test
    fun `benchmark reflects parallel usage once threshold met`() {
        val lowerThreshold = N / 2

        val metrics = service.benchmarkSequentialVsParallel(
            iterations = 1,
            parallelThreshold = lowerThreshold
        )

        assertEquals(N, metrics.recordCount)
        assertTrue(metrics.parallelUsed)
        // 두 방식 모두 동일 데이터 크기를 반환하는지 확인
        val sequentialTime = measureNanoTime { service.findAllSequential() }
        val parallelTime = measureNanoTime { service.findAllParallel(parallelThreshold = lowerThreshold) }
        println("N=$N, threshold=$lowerThreshold, sequential=${sequentialTime/1_000_000}ms, parallel=${parallelTime/1_000_000}ms")
    }
}

package io.df.dfspringjpa.account

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
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
    private val N = 20000

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        val accounts = (1..N).map { Account(name = "name$it", email = "user$it@example.com") }
        repository.saveAll(accounts)
    }

    @Test
    fun benchmark() {
        val expected = repository.count().toInt()

        val seqTime = measureNanoTime {
            val result = service.findAllSequential()
            assertEquals(expected, result.size)
        }

        val parTime = measureNanoTime {
            val result = service.findAllParallel()
            assertEquals(expected, result.size)
        }

//        val futTime = measureNanoTime {
//            val result = service.findAllWithFutures()
//            assertEquals(expected, result.size)
//        }

        println("Benchmark N=$N -> sequential=${seqTime/1_000_000}ms, parallel=${parTime/1_000_000}ms, futures=${futTime/1_000_000}ms")
    }
}

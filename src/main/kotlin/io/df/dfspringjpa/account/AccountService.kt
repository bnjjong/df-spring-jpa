package io.df.dfspringjpa.account

import org.springframework.stereotype.Service
import java.time.Duration
import java.util.stream.Collectors

@Service
open class AccountService(private val repository: AccountRepository) {

    companion object {
        private const val DEFAULT_PARALLEL_THRESHOLD = 1_000
    }

    data class FetchPerformance(
        val recordCount: Int,
        val sequentialAverage: Duration,
        val parallelAverage: Duration,
        val parallelUsed: Boolean
    )

    // 단건 조회
    fun findById(id: Long): Account? = repository.findById(id).orElse(null)

    // 전체를 한 번에 조회 (단일 쿼리)
    fun findAllSequential(): List<Account> = repository.findAllAccounts()

    // 전체 id를 가져와서 각 id별로 개별 조회를 병렬로 수행 (여러 쿼리, 병렬)
    fun findAllParallel(parallelThreshold: Int = DEFAULT_PARALLEL_THRESHOLD): List<Account> {
        val ids = repository.findAllIds()
        return fetchParallelByIds(ids, parallelThreshold)
    }

    /**
     * 순차 vs 병렬 시나리오를 간단히 벤치마크.
     * 작은 데이터셋에서는 병렬 처리가 오히려 느릴 수 있다는 점을 드러내기 위한 용도.
     */
    fun benchmarkSequentialVsParallel(
        iterations: Int = 3,
        parallelThreshold: Int = DEFAULT_PARALLEL_THRESHOLD
    ): FetchPerformance {
        require(iterations > 0) { "iterations must be greater than zero" }

        val ids = repository.findAllIds()
        val recordCount = ids.size

        val sequentialNanos = measure(iterations) {
            fetchSequentialByIds(ids)
        }
        val parallelNanos = measure(iterations) {
            fetchParallelByIds(ids, parallelThreshold)
        }

        return FetchPerformance(
            recordCount = recordCount,
            sequentialAverage = Duration.ofNanos(sequentialNanos / iterations),
            parallelAverage = Duration.ofNanos(parallelNanos / iterations),
            parallelUsed = recordCount >= parallelThreshold
        )
    }

    private fun fetchSequentialByIds(ids: Collection<Long>): List<Account> = ids.mapNotNull {
        repository.findById(it).orElse(null)
    }

    private fun fetchParallelByIds(ids: List<Long>, parallelThreshold: Int): List<Account> {
        if (ids.size < parallelThreshold) {
            return fetchSequentialByIds(ids)
        }

        return ids.parallelStream()
            .map { repository.findById(it).orElse(null) }
            .collect(Collectors.toList())
            .filterNotNull()
    }

    private inline fun measure(iterations: Int, block: () -> List<Account>): Long =
        (0 until iterations).sumOf {
            val start = System.nanoTime()
            block()
            System.nanoTime() - start
        }
}

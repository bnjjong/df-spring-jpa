package io.df.dfspringjpa.account

import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
open class AccountService(private val repository: AccountRepository) {
    // 단건 조회
    fun findById(id: Long): Account? = repository.findById(id).orElse(null)

    // 전체를 한 번에 조회 (단일 쿼리)
    fun findAllSequential(): List<Account> = repository.findAllAccounts()

    // 전체 id를 가져와서 각 id별로 개별 조회를 병렬로 수행 (여러 쿼리, 병렬)
    fun findAllParallel(): List<Account> {
        val ids = repository.findAllIds()
        return ids.parallelStream()
            .map { repository.findById(it).orElse(null) }
            .filter { it != null }
            .collect(Collectors.toList())
            .map { it!! }
    }

    // CompletableFuture를 사용한 병렬 조회(동기적으로 결과 합침)
    fun findAllWithFutures(): List<Account> {
        val ids = repository.findAllIds()
        val futures = ids.map { id ->
            java.util.concurrent.CompletableFuture.supplyAsync {
                repository.findById(id).orElse(null)
            }
        }
        return futures.map { it.join() }.filterNotNull()
    }
}

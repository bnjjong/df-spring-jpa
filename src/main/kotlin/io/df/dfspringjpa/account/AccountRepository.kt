package io.df.dfspringjpa.account

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a")
    fun findAllAccounts(): List<Account>

    @Query("SELECT a.id FROM Account a")
    fun findAllIds(): List<Long>
}

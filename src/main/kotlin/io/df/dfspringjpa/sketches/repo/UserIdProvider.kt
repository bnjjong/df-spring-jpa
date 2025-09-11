package io.df.dfspringjpa.sketches.repo

/**
 * Abstraction to provide user IDs that match a (hardcoded) segment condition.
 * In a real app this would be implemented via JPA repository and a JPQL query.
 */
fun interface UserIdProvider {
    fun findUserIdsMatchingSegment(): List<Long>
}
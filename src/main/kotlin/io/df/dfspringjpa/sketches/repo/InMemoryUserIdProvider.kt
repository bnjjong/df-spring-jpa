package io.df.dfspringjpa.sketches.repo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Random

/**
 * Simple in-memory provider for demo purposes.
 * Generates a deterministic set of user IDs that "match the segment".
 */
@Component
class InMemoryUserIdProvider(
    @Value("\${sketches.inmemory.size:100000}") private val size: Int
) : UserIdProvider {

    private val ids: List<Long> by lazy { generateIds(size) }


    override fun findUserIdsMatchingSegment(): List<Long> = ids

    private fun generateIds(n: Int): List<Long> {
        // Deterministic pseudo-random but stable across runs
        val rnd = Random(42L)
        return List(n.coerceAtLeast(0)) { idx ->
            // generate positive unique-ish longs
            val next = rnd.nextLong().let { if (it < 0) -it else it }
            // ensure not zero and spread
            (next + idx + 1L)
        }
    }
}
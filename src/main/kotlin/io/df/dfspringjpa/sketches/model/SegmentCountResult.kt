package io.df.dfspringjpa.sketches.model

/**
 * API response model for approximate audience segment counts using HyperLogLog.
 */
data class SegmentCountResult(
    val estimatedCount: Long,
    val lowerBound: Long,
    val upperBound: Long,
    val relativeError: Double,
    val memoryUsed: Int
)
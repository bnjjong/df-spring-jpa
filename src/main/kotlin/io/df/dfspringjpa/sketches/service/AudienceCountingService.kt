package io.df.dfspringjpa.sketches.service

import io.df.dfspringjpa.sketches.model.SegmentCountResult
import io.df.dfspringjpa.sketches.repo.UserIdProvider
import org.apache.datasketches.hll.HllSketch
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.system.measureTimeMillis

@Service
class AudienceCountingService(
    private val userIdProvider: UserIdProvider
) {
    private val lgConfigK = 12 // ~1.6% std error, ~4KB memory
    @Volatile private var segmentSketch: HllSketch = HllSketch(lgConfigK)

    /**
     * Rebuilds the segment sketch from the user IDs matching the hardcoded segment.
     */
    fun updateSegmentSketch(): String {
        var lastEstimate = 0.0
        val elapsed = measureTimeMillis {
            val sketch = HllSketch(lgConfigK)
            val userIds = userIdProvider.findUserIdsMatchingSegment()
            userIds.forEach { id -> sketch.update(id.toString()) }
            segmentSketch = sketch
            lastEstimate = segmentSketch.estimate
        }
        return "Segment sketch updated with ${lastEstimate.toLong()} estimated users in ${elapsed}ms"
    }

    /**
     * Returns approximate segment count and bounds.
     */
    fun getEstimatedSegmentCount(): SegmentCountResult {
        val est = segmentSketch.estimate
        val lb = segmentSketch.getLowerBound(1)
        val ub = segmentSketch.getUpperBound(1)
        val relErr = if (est > 0.0) max(est - lb, ub - est) / est else 0.0
        val mem = segmentSketch.toCompactByteArray().size
        return SegmentCountResult(
            estimatedCount = est.toLong(),
            lowerBound = lb.toLong(),
            upperBound = ub.toLong(),
            relativeError = relErr,
            memoryUsed = mem
        )
    }
}
package io.df.dto.benchmark

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.LongSummaryStatistics
import kotlin.random.Random

@Component
class DtoConversionBenchmark : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val entities = generateEntities(ENTITY_COUNT)
        warmUp(entities)

        val summary = LongSummaryStatistics()
        val overallStart = System.nanoTime()
        val dtos = entities.map { entity ->
            val start = System.nanoTime()
            val dto = entity.toDto()
            val elapsed = System.nanoTime() - start
            summary.accept(elapsed)
            dto
        }
        val overallElapsed = System.nanoTime() - overallStart

        log.info("Converted {} entities into DTOs", summary.count)
        log.info(
            "Conversion time total={} average={} min={} max={} (overall={} including collection)",
            formatNanos(summary.sum),
            formatNanos(summary.average),
            formatNanos(summary.min),
            formatNanos(summary.max),
            formatNanos(overallElapsed.toDouble()),
        )
        log.debug("First DTO sample: {}", dtos.firstOrNull())
    }

    private fun warmUp(entities: List<SampleEntity>) {
        repeat(WARM_UP_ITERATIONS) {
            entities.forEach { it.toDto() }
        }
    }

    private fun generateEntities(count: Int): List<SampleEntity> = buildList(count) {
        repeat(count) { index ->
            add(
                SampleEntity(
                    id = index + 1L,
                    name = randomString(24),
                    description = randomParagraph(4),
                    createdAt = LocalDateTime.now().minusDays(Random.nextLong(0, 365)),
                    score = Random.nextDouble(0.0, 10_000.0),
                    tags = List(TAG_COUNT) { randomString(8) },
                    metadata = mutableMapOf<String, String>().apply {
                        repeat(METADATA_COUNT) { metaIndex ->
                            this["key-$metaIndex"] = randomParagraph(2)
                        }
                    },
                ),
            )
        }
    }

    private fun SampleEntity.toDto(): SampleDto {
        val normalizedTags = tags
            .asSequence()
            .map { it.lowercase() }
            .sorted()
            .toList()

        val metadataSummary = metadata.entries
            .sortedBy { it.key }
            .joinToString(separator = " | ") { (key, value) ->
                val trimmedValue = value.take(MAX_METADATA_PREVIEW)
                "$key:$trimmedValue"
            }

        return SampleDto(
            id = id,
            displayName = name.uppercase(),
            createdAtEpoch = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
            scoreBucket = score.toScoreBucket(),
            tagSummary = normalizedTags,
            metadataDigest = metadataSummary,
        )
    }

    private fun Double.toScoreBucket(): String {
        val bucket = (this / SCORE_BUCKET_SIZE).toInt()
        val rangeStart = bucket * SCORE_BUCKET_SIZE
        val rangeEnd = rangeStart + SCORE_BUCKET_SIZE
        return "$rangeStart-$rangeEnd"
    }

    private fun formatNanos(value: Long): String = formatNanos(value.toDouble())

    private fun formatNanos(value: Double): String {
        val millis = value / NANOS_IN_MILLI
        return String.format("%.3f ms", millis)
    }

    private fun randomString(length: Int): String = buildString(length) {
        repeat(length) {
            append(ALPHABET[Random.nextInt(ALPHABET.length)])
        }
    }

    private fun randomParagraph(words: Int): String = buildString {
        repeat(words) { index ->
            append(randomString(Random.nextInt(4, 12)))
            if (index < words - 1) append(' ')
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(DtoConversionBenchmark::class.java)
        private const val ENTITY_COUNT = 1_000_000
        private const val WARM_UP_ITERATIONS = 2
        private const val TAG_COUNT = 10
        private const val METADATA_COUNT = 5
        private const val SCORE_BUCKET_SIZE = 50
        private const val MAX_METADATA_PREVIEW = 32
        private const val NANOS_IN_MILLI = 1_000_000.0
        private const val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    }
}

data class SampleEntity(
    val id: Long,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val score: Double,
    val tags: List<String>,
    val metadata: Map<String, String>,
)

data class SampleDto(
    val id: Long,
    val displayName: String,
    val createdAtEpoch: Long,
    val scoreBucket: String,
    val tagSummary: List<String>,
    val metadataDigest: String,
)

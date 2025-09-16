package io.df.dfspringjpa.sketches.service

import io.df.dfspringjpa.sketches.constants.ThetaConstants
import io.df.dfspringjpa.sketches.constants.ThetaConstants.SKETCH_SIZE_LOG_K
import io.df.dfspringjpa.sketches.constants.ThetaConstants.THETA_SKETCH_SEED
import io.df.dfspringjpa.sketches.domain.Audience
import io.df.dfspringjpa.sketches.domain.AudienceTheta
import io.df.dfspringjpa.sketches.repo.AudienceRepository
import io.df.dfspringjpa.sketches.repo.AudienceThetaRepository
import org.apache.datasketches.memory.Memory
import org.apache.datasketches.theta.CompactSketch
import org.apache.datasketches.theta.Sketches
import org.apache.datasketches.theta.UpdateSketch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AudienceService(
    private val audienceRepository: AudienceRepository,
    private val audienceThetaRepository: AudienceThetaRepository
) {
    fun findAll(): List<Audience> {
        return audienceRepository.findAll()
    }

    fun findThetaAudienceGender(): Double {
        val data = audienceThetaRepository.findById("남자").orElse(null).thetaSketch
        // 바이트 배열을 CompactSketch로 역직렬화
        val sketch = CompactSketch.heapify(
            Memory.wrap(data),
            THETA_SKETCH_SEED
        )

        return sketch.estimate
    }

    fun findThetaTest(): Double {
        val intersection = Sketches.setOperationBuilder()
            .setNominalEntries(1 shl SKETCH_SIZE_LOG_K)
            .setSeed(THETA_SKETCH_SEED)
            .buildIntersection()
        val genderData = audienceThetaRepository.findById("남자").orElse(null).thetaSketch
        val memberShipData = audienceThetaRepository.findById("DIAMOND").orElse(null).thetaSketch

        intersection.intersect(
            CompactSketch.heapify(
                Memory.wrap(genderData),
                THETA_SKETCH_SEED
            ))
        intersection.intersect(CompactSketch.heapify(
            Memory.wrap(memberShipData),
            THETA_SKETCH_SEED
        ))

        return intersection.result.estimate

    }

    @Transactional
    fun saveThetaAudienceGender() {
        val genderMap = mutableMapOf<String, UpdateSketch>()
        findAll().forEach { audience ->
            val key = audience.gender.uppercase()
            val sketch = genderMap.computeIfAbsent(key) {
                UpdateSketch.builder()
                    /**
                     *
                     * 스케치(ThetaSketch)가 "얼마나 많은 샘플을 잡아둘지"를 정하는 값이에요.
                     * 1 shl x는 2의 x제곱을 의미합니다.
                     * 예를 들어 SKETCH_SIZE_LOG_K = 12라면 → 1 shl 12 = 4096이 돼요.
                     *
                     * 이 숫자가 클수록 결과가 더 정확해져요. 하지만 대신 메모리를 더 많이 사용해요.
                     * 작은 값 → 메모리 적게 사용, 대신 오차율이 커짐.
                     * 큰 값 → 메모리 많이 사용, 대신 오차율이 작아짐.
                     * 쉽게 비유하면:
                     * “설문조사를 몇 명한테 할까?” 정하는 것과 비슷해요. 100명만 조사하면 대충 경향은 알 수 있지만 오차가 크고,
                     * 10,000명을 조사하면 훨씬 정확하지만 비용이 많이 들죠.
                     */
                    .setNominalEntries(1 shl ThetaConstants.SKETCH_SIZE_LOG_K)
                    .setSeed(ThetaConstants.THETA_SKETCH_SEED)
                    .build()
            }
            sketch.update(audience.id.toString())
        }
        genderMap.forEach { (key, value) ->
            val theta = AudienceTheta(
                key,
                value.compact().toByteArray()
            )
            audienceThetaRepository.save(theta)
        }
    }

    @Transactional
    fun saveThetaAudience() {
        val genderMap = mutableMapOf<String, UpdateSketch>()
        findAll().forEach { audience ->
            val key = audience.membershipLevel.uppercase()
            val sketch = genderMap.computeIfAbsent(key) {
                UpdateSketch.builder()
                    .setNominalEntries(1 shl ThetaConstants.SKETCH_SIZE_LOG_K)
                    .setSeed(ThetaConstants.THETA_SKETCH_SEED)
                    .build()
            }
            sketch.update(audience.id.toString())
        }
        genderMap.forEach { (key, value) ->
            val theta = AudienceTheta(
                key,
                value.compact().toByteArray()
            )
            audienceThetaRepository.save(theta)
        }
    }
}
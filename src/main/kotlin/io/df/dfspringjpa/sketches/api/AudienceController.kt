package io.df.dfspringjpa.sketches.api

import io.df.dfspringjpa.sketches.model.SegmentCountResult
import io.df.dfspringjpa.sketches.service.AudienceCountingService
import io.df.dfspringjpa.sketches.service.AudienceService
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/audience")
class AudienceController(
    private val audienceService: AudienceService,
    private val audienceCountingService: AudienceCountingService
) {
    @PostMapping("/theta/gender")
    fun saveThetaGender() {
        audienceService.saveThetaAudienceGender()
    }

    @PostMapping("/theta/all")
    fun saveThetaAll() {
        audienceService.saveThetaAudience()
    }

    @GetMapping("/theta/gender")
    fun getThetaGender(): Double {
        return audienceService.findThetaAudienceGender()
    }

    @GetMapping("/theta/all")
    fun getThetaAll(): Double {
        return audienceService.findThetaTest()
    }

    @GetMapping("/segment/count")
    fun getSegmentCount(): SegmentCountResult = audienceCountingService.getEstimatedSegmentCount()

    @PostMapping("/segment/update")
    fun updateSegmentSketch(): Map<String, String> =
        mapOf("message" to audienceCountingService.updateSegmentSketch())
}
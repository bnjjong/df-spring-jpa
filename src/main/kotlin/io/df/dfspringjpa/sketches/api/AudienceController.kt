package io.df.dfspringjpa.sketches.api

import io.df.dfspringjpa.sketches.model.SegmentCountResult
import io.df.dfspringjpa.sketches.service.AudienceCountingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/audience")
class AudienceController(
    private val audienceCountingService: AudienceCountingService
) {
    @GetMapping("/segment/count")
    fun getSegmentCount(): SegmentCountResult = audienceCountingService.getEstimatedSegmentCount()

    @PostMapping("/segment/update")
    fun updateSegmentSketch(): Map<String, String> =
        mapOf("message" to audienceCountingService.updateSegmentSketch())
}
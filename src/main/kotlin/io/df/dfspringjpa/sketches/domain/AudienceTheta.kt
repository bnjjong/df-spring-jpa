package io.df.dfspringjpa.sketches.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "audience_theta")
class AudienceTheta(
    @Id
    val segmentKey: String,

    @Lob
    @Column(name = "theta_sketch", columnDefinition = "LONGBLOB")
    val thetaSketch: ByteArray
) {
}
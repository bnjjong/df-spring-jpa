package io.df.dfspringjpa.sketches.domain

import io.df.dfspringjpa.common.EntityId
import jakarta.persistence.Embeddable
import java.util.UUID

@Embeddable
data class AudienceId (
    override val id: UUID,
): EntityId<UUID> {
    constructor(): this(UUID.randomUUID())
    constructor(id: String) : this(UUID.fromString(id))
}
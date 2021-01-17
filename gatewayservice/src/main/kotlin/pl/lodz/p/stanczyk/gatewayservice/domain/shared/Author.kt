package pl.lodz.p.stanczyk.gatewayservice.domain.shared

import java.util.UUID

data class Author(
    val id: UUID,
    val name: String?
)
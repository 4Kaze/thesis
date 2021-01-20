package pl.lodz.p.stanczyk.gatewayservice.domain.user

import java.util.UUID

data class User(
    val id: UUID,
    val name: String?
)
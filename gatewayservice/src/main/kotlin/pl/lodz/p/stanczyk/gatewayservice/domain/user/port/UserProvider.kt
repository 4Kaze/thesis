package pl.lodz.p.stanczyk.gatewayservice.domain.user.port

import pl.lodz.p.stanczyk.gatewayservice.domain.user.User
import java.util.UUID

interface UserProvider {
    fun findByIds(userIds: Set<UUID>): Map<UUID, User>
}
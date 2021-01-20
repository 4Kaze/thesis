package pl.lodz.p.stanczyk.gatewayservice.adapter

import feign.FeignException
import org.slf4j.LoggerFactory
import pl.lodz.p.stanczyk.gatewayservice.domain.user.User
import pl.lodz.p.stanczyk.gatewayservice.domain.user.port.UserProvider
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.UserClientResponseUser
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.UserServiceClient
import java.util.UUID

class UserServiceAdapter(private val userServiceClient: UserServiceClient) : UserProvider {
    override fun findByIds(userIds: Set<UUID>): Map<UUID, User> =
        try {
            userServiceClient.getUserNames(userIds).map {
                it.id to it.toDomain()
            }.toMap()
        } catch (exception: FeignException) {
            logger.error("Error fetching users from user service: ${exception.message}")
            userIds.map { it to
                User(
                    id = it,
                    name = null
                )
            }.toMap()
        }

    private fun UserClientResponseUser.toDomain() = User(
        id = this.id,
        name = this.name
    )

    companion object {
        val logger = LoggerFactory.getLogger(UserServiceAdapter::class.java);
    }
}
package pl.lodz.p.stanczyk.gatewayservice.adapter

import feign.FeignException
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
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }

    private fun UserClientResponseUser.toDomain() = User(
        id = this.id,
        name = this.name
    )
}
package pl.lodz.p.stanczyk.gatewayservice.integration.builder

import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.UserClientResponseUser

class UserBuilder {
    static UserClientResponseUser aUser(String id, String name = "User Name") {
        new UserClientResponseUser(
                UUID.fromString(id),
                name
        )
    }
}

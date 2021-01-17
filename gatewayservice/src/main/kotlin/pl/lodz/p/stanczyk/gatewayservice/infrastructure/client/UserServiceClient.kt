package pl.lodz.p.stanczyk.gatewayservice.infrastructure.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.Instant
import java.util.UUID

@FeignClient(name = "UserService", url = "\${services.user.url}")
interface UserServiceClient {
    @GetMapping(
        path = ["/users"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUserNames(@RequestParam ids: Set<UUID>): List<UserClientResponseUser>
}

data class UserClientResponseUser(
    val id: UUID,
    val name: String
)
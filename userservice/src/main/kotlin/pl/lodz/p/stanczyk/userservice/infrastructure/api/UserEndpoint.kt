package pl.lodz.p.stanczyk.userservice.infrastructure.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pl.lodz.p.stanczyk.userservice.adapters.UserFacade

@RestController
class UserEndpoint(val userFacade: UserFacade) {
    @PostMapping("/users")
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        val response = userFacade.createUser(request.toDto())
        return UserResponse.fromDto(response)
    }
}
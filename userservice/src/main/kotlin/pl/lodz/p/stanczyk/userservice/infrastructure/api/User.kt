package pl.lodz.p.stanczyk.userservice.infrastructure.api

import pl.lodz.p.stanczyk.userservice.adapters.UserDto
import pl.lodz.p.stanczyk.userservice.adapters.UserTypeDto
import java.time.Instant
import java.util.*

data class UserResponse(
        val id: UUID,
        val userType: UserTypeResponse,
        val createdAt: Instant,
        val updatedAt: Instant?,
        val login: String,
        val name: String
) {
    companion object {
        fun fromDto(userDto: UserDto) =
                UserResponse(
                        id = userDto.id,
                        userType = UserTypeResponse.fromDto(userDto.userType),
                        login = userDto.login,
                        name = userDto.name,
                        createdAt = userDto.createdAt,
                        updatedAt = userDto.updatedAt
                )
    }
}

enum class UserTypeResponse {
    ADMIN, WRITER, READER;

    companion object {
        fun fromDto(userTypeDto: UserTypeDto): UserTypeResponse = when (userTypeDto) {
            UserTypeDto.ADMIN -> ADMIN
            UserTypeDto.WRITER -> WRITER
            UserTypeDto.READER -> READER
        }
    }
}
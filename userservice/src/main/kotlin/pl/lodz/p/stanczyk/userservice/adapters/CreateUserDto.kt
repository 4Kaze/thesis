package pl.lodz.p.stanczyk.userservice.adapters

import java.time.Instant
import java.util.*

data class CreateUserDto(val login: String, val password: String, val name: String)
data class UserDto(
        val id: UUID,
        val userType: UserTypeDto,
        val createdAt: Instant,
        val updatedAt: Instant?,
        val login: String,
        val name: String,
)
enum class UserTypeDto {
    ADMIN, WRITER, READER
}
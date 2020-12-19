package pl.lodz.p.stanczyk.userservice.domain

import java.time.Instant
import java.util.*

data class User(
        val id: UserId,
        val userType: UserType,
        val createdAt: Instant,
        val updatedAt: Instant?,
        val login: String,
        val password: String,
        val name: String
)

data class UserId private constructor(val value: UUID) {
    companion object {
        fun of(id: UUID) = UserId(id)
    }
}

enum class UserType {
    ADMIN, WRITER, READER
}

data class CreateUser(
        val login: String,
        val password: String,
        val name: String,
        val userType: UserType
)
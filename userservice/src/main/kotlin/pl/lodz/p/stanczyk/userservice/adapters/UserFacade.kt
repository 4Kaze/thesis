package pl.lodz.p.stanczyk.userservice.adapters

import pl.lodz.p.stanczyk.userservice.domain.CreateUser
import pl.lodz.p.stanczyk.userservice.domain.User
import pl.lodz.p.stanczyk.userservice.domain.UserService
import pl.lodz.p.stanczyk.userservice.domain.UserType

class UserFacade(val userService: UserService) {
    fun createUser(createUserDto: CreateUserDto): UserDto {
        val user = userService.createUser(createUserDto.toDomain())
        return user.toDto()
    }

    private fun CreateUserDto.toDomain() = CreateUser(
            login = this.login,
            name = this.name,
            password = this.password,
            userType = UserType.READER
    )

    private fun User.toDto() = UserDto(
            id = this.id.value,
            userType = this.userType.toDto(),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            name = this.name,
            login = this.login
    )

    private fun UserType.toDto() = when(this) {
        UserType.ADMIN -> UserTypeDto.ADMIN
        UserType.WRITER -> UserTypeDto.WRITER
        UserType.READER -> UserTypeDto.READER
    }
}
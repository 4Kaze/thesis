package pl.lodz.p.stanczyk.userservice.infrastructure.api

import org.hibernate.validator.constraints.Length
import pl.lodz.p.stanczyk.userservice.adapters.CreateUserDto
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class CreateUserRequest(
        @Pattern(regexp = "[^\\s]")
        @Length(min = 6, max = 20)
        val login: String,
        @Length(min = 8, max = 50)
        val password: String,
        @Length(min = 1, max = 50)
        @NotBlank
        val name: String
) {
    fun toDto(): CreateUserDto =
            CreateUserDto(
                    login = this.login,
                    password = this.password,
                    name = this.name
            )
}
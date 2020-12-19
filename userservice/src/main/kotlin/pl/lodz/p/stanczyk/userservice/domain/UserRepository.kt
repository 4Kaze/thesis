package pl.lodz.p.stanczyk.userservice.domain

interface UserRepository {
    fun create(createUser: CreateUser): User
}
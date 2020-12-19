package pl.lodz.p.stanczyk.userservice.domain

interface UserService {
    fun createUser(user: CreateUser): User
}
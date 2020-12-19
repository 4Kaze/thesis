package pl.lodz.p.stanczyk.userservice.api

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CreateUserRequestBuilder {
    String login = "TestLogin"
    String password = "StrongPassword1234!"
    String name = "Test Login"

    static CreateUserRequestBuilder aCreateUserRequest() {
        new CreateUserRequestBuilder()
    }

    Map build() {
        [
                "login": login,
                "password": password,
                "name": name
        ]
    }
}

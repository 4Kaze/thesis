package pl.lodz.p.stanczyk.userservice

import pl.lodz.p.stanczyk.userservice.adapters.UserFacade
import pl.lodz.p.stanczyk.userservice.domain.UserService
import pl.lodz.p.stanczyk.userservice.domain.UserServiceImpl
import pl.lodz.p.stanczyk.userservice.infrastructure.api.UserEndpoint
import spock.lang.Specification
import spock.lang.Subject

class EndpointSpec extends Specification {
    @Subject
    UserEndpoint usersEndpoint
    private UserFacade userFacade
    private UserService userService
    private 

    void setup() {
        userService = new UserServiceImpl()
        userFacade = new UserFacade()
        usersEndpoint = new UserEndpoint()
    }
}

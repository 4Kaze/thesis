package pl.lodz.p.stanczyk.userservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
class IntegrationSpec extends Specification {
    @LocalServerPort
    def SERVER_PORT
    def SERVER_URL = "localhost"

    @Shared
    def client = new RESTClient("$SERVER_URL:$SERVER_PORT")
}

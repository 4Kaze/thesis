package pl.lodz.p.stanczyk.gatewayservice.integration.stub

import groovy.json.JsonOutput
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec.wireMockServer

trait UserServiceStub {
    void stubGetUsers(List<String> userIds, Object response, int status = 200) {
        wireMockServer.stubFor(get("/user-service/users?ids=${userIds.join("&ids=")}")
                .willReturn(
                        aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(JsonOutput.toJson(response))
                                .withStatus(status)
                )
        )
    }
}
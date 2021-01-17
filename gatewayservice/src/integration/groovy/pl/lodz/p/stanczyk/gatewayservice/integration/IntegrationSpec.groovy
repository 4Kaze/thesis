package pl.lodz.p.stanczyk.gatewayservice.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import groovy.json.JsonOutput
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jws.AlgorithmIdentifiers
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

@AutoConfigureDataMongo
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [IntegrationTestConfiguration.class])
@ActiveProfiles("integration")
class IntegrationSpec extends Specification {
    static final String wiremockUrl = "http://localhost:8079"
    static final String realmName = "blog"
    @LocalServerPort
    def SERVER_PORT
    def SERVER_URL = "localhost"
    @Shared
    TestRestTemplate restTemplate = new TestRestTemplate()
    static WireMockServer wireMockServer
    @Shared
    static RsaJsonWebKey rsaJsonWebKey

    def setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8079))
        wireMockServer.start()
        stubOAuth()
    }

    def setupSpec() {
        if(rsaJsonWebKey == null) setupKey()
    }

    def cleanup() {
        wireMockServer.stop()
    }

    String url(String path) {
        "http://$SERVER_URL:$SERVER_PORT$path"
    }

    static def setupKey() {
        rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048).with(true) {
            keyId = "k1"
            algorithm = AlgorithmIdentifiers.RSA_USING_SHA256
            use = "sig"
        } as RsaJsonWebKey
    }

    static void stubOAuth() {
        def openIdConfig = JsonOutput.toJson([
                "issuer": "$wiremockUrl/auth/realms/$realmName",
                "authorization_endpoint": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/auth",
                "token_endpoint": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/token",
                "introspection_endpoint": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/token/introspect",
                "userinfo_endpoint": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/userinfo",
                "end_session_endpoint": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/logout",
                "jwks_uri": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/certs",
                "check_session_iframe": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/login-status-iframe.html",
                "registration_endpoint": "$wiremockUrl/auth/realms/$realmName/clients-registrations/openid-connect",
                "revocation_endpoint": "$wiremockUrl/auth/realms/$realmName/protocol/openid-connect/revoke",
        ])
        wireMockServer.stubFor(get(urlEqualTo("/auth/realms/$realmName/.well-known/openid-configuration"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(openIdConfig)
                )
        );
        wireMockServer.stubFor(get(urlEqualTo("/auth/realms/$realmName/protocol/openid-connect/certs"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(new JsonWebKeySet(rsaJsonWebKey).toJson())
                )
        );
    }
}

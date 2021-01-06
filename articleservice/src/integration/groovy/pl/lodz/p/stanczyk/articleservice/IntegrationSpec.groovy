package pl.lodz.p.stanczyk.articleservice

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import groovy.json.JsonOutput
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jws.AlgorithmIdentifiers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit

import static com.github.tomakehurst.wiremock.client.WireMock.*

@AutoConfigureDataMongo
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [IntegrationTestConfiguration.class])
@ActiveProfiles("integration")
class IntegrationSpec extends Specification {
    static final String keycloakUrl = "http://localhost:8079"
    static final String realmName = "blog"
    @LocalServerPort
    def SERVER_PORT
    def SERVER_URL = "localhost"
    @Shared
    TestRestTemplate restTemplate = new TestRestTemplate()
    @Autowired
    MongoTemplate mongoTemplate
    WireMockServer wireMockServer
    @Shared
    static RsaJsonWebKey rsaJsonWebKey

    def setup() {
        theTimeIs(null)
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8079))
        wireMockServer.start()
        stubOAuth()
    }

    def setupSpec() {
        if(rsaJsonWebKey == null) setupKey()
    }

    def cleanup() {
        mongoTemplate.getDb().drop()
        wireMockServer.stop()
    }

    String url(String path) {
        "http://$SERVER_URL:$SERVER_PORT$path"
    }

    static void theTimeIs(Instant time) {
        IntegrationTestConfiguration.testTime = time
    }

    static Instant nowInMillis() {
        Instant.now().truncatedTo(ChronoUnit.MILLIS)
    }

    static def setupKey() {
        rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048).with(true) {
            keyId = "k1"
            algorithm = AlgorithmIdentifiers.RSA_USING_SHA256
            use = "sig"
        } as RsaJsonWebKey
    }

    void stubOAuth() {
        def openIdConfig = JsonOutput.toJson([
                "issuer": "$keycloakUrl/auth/realms/$realmName",
                "authorization_endpoint": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/auth",
                "token_endpoint": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/token",
                "introspection_endpoint": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/token/introspect",
                "userinfo_endpoint": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/userinfo",
                "end_session_endpoint": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/logout",
                "jwks_uri": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/certs",
                "check_session_iframe": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/login-status-iframe.html",
                "registration_endpoint": "$keycloakUrl/auth/realms/$realmName/clients-registrations/openid-connect",
                "revocation_endpoint": "$keycloakUrl/auth/realms/$realmName/protocol/openid-connect/revoke",
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
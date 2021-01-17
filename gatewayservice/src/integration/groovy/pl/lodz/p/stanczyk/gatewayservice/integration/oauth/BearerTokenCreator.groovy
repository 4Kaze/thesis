package pl.lodz.p.stanczyk.gatewayservice.integration.oauth

import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec

import static pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec.realmName
import static pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec.rsaJsonWebKey
import static pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec.wiremockUrl

trait BearerTokenCreator {
    private static def JWT_ID = UUID.fromString("f3d12098-e2df-453b-83e6-f6474d1df1f7")

    String oAuthTokenFor(String userId, String username, String name, String... roles) {
        JwtClaims claims = new JwtClaims().with(true) {
            jwtId = JWT_ID
            expirationTimeMinutesInTheFuture = 10
            audience = "account"
            issuer = "$wiremockUrl/auth/realms/$realmName"
            subject = userId
            setClaim("typ", "Bearer")
            setClaim("azp", "gateway-service")
            setClaim("session_state", "b7ae9dbf-1f85-4dba-82bb-6ec36b178757")
            setClaim("acr", "1")
            setClaim("realm_access", ["roles": roles])
            setClaim("scope", "roles profile")
            setClaim("name", name)
            setClaim("preferred_username", username)
            setIssuedAtToNow()
        }
        return new JsonWebSignature().with(true) {
            payload = claims.toJson()
            key = rsaJsonWebKey.privateKey
            keyIdHeaderValue = rsaJsonWebKey.keyId
            algorithmHeaderValue = rsaJsonWebKey.algorithm
            setHeader("typ", "JWT")
        }.getCompactSerialization()
    }
}
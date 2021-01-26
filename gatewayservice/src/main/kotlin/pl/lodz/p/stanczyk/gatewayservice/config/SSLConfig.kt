package pl.lodz.p.stanczyk.gatewayservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct

@ConfigurationProperties(prefix = "ssl.key-store")
@ConstructorBinding
class SSLConfig(
    private val path: String,
    private val password: String,
    private val alias: String
) {
    @PostConstruct
    private fun configureSSL() {
        System.setProperty("javax.net.ssl.keyStore", path)
        System.setProperty("javax.net.ssl.keyStorePassword", password)
    }
}
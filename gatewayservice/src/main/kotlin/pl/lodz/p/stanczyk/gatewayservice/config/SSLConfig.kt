package pl.lodz.p.stanczyk.gatewayservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct




@Configuration
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
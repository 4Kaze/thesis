package pl.lodz.p.stanczyk.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import pl.lodz.p.stanczyk.gatewayservice.config.SSLConfig

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(SSLConfig::class)
class GatewayServiceApplication

fun main(args: Array<String>) {
    runApplication<GatewayServiceApplication>(*args)
}

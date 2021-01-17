package pl.lodz.p.stanczyk.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@EnableZuulProxy
@SpringBootApplication
class ApigatewayApplication

fun main(args: Array<String>) {
    runApplication<ApigatewayApplication>(*args)
}

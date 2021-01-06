package pl.lodz.p.stanczyk.commentservice.integration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pl.lodz.p.stanczyk.commentservice.infrastructure.InstantNowSupplier

import java.time.Instant

@TestConfiguration
class IntegrationTestConfiguration {
    static Instant testTime

    @Bean
    InstantNowSupplier instantNowSupplier() {
        new TestInstantNowSupplier()
    }
}

class TestInstantNowSupplier implements InstantNowSupplier {
    @Override
    Instant get() {
        return IntegrationTestConfiguration.testTime == null ? Instant.now() : IntegrationTestConfiguration.testTime
    }
}
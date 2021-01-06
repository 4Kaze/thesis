package pl.lodz.p.stanczyk.articleservice.infrastructure

import java.time.Instant

class StaticInstantNowSupplier(private val instant: Instant) : InstantNowSupplier {
    override fun get() = instant
}

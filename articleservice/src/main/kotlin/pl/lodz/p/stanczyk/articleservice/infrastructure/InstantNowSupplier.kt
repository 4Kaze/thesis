package pl.lodz.p.stanczyk.articleservice.infrastructure

import java.time.Instant
import java.util.function.Supplier

interface InstantNowSupplier : Supplier<Instant>

class InstantNowSupplierImpl : InstantNowSupplier {
    override fun get(): Instant = Instant.now()
}

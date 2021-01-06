package pl.lodz.p.stanczyk.commentservice.infrastructure

import java.time.Instant
import java.util.function.Supplier

interface InstantNowSupplier : Supplier<Instant>

class InstantNowSupplierImpl : InstantNowSupplier {
    override fun get(): Instant = Instant.now()
}

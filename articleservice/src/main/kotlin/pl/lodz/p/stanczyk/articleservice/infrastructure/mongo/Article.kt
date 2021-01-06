package pl.lodz.p.stanczyk.articleservice.infrastructure.mongo

import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.UUID

data class Article(
    @Id val id: String?,
    val title: String,
    val content: String,
    val publicationDate: Instant,
    val updatedAt: Instant?,
    val authorId: UUID,
)

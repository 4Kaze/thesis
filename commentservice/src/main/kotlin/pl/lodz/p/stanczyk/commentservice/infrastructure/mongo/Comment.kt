package pl.lodz.p.stanczyk.commentservice.infrastructure.mongo

import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.UUID

data class Comment(
    @Id val id: String?,
    val content: String,
    val publicationDate: Instant,
    val authorId: UUID,
    val articleId: UUID
)

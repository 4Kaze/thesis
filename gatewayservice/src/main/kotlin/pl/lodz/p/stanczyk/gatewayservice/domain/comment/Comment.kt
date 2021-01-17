package pl.lodz.p.stanczyk.gatewayservice.domain.comment

import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import java.time.Instant
import java.util.UUID

data class CommentDraft(
    val articleId: UUID,
    val content: String
)

data class Comment(
    val id: UUID,
    val author: Author,
    val content: String,
    val publicationDate: Instant
) {
    fun patchWithAuthor(author: Author) = this.copy(author = author)
}

data class CommentList(
    val comments: List<Comment>,
    val count: Int
)
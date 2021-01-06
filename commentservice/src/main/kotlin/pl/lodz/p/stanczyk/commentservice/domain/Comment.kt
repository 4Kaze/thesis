package pl.lodz.p.stanczyk.commentservice.domain

import java.time.Instant
import java.util.UUID

data class CommentDraft(
    val authorId: AuthorId,
    val articleId: ArticleId,
    val content: String
)

data class Comment(
    val id: CommentId?,
    val authorId: AuthorId,
    val content: String,
    val publicationDate: Instant,
    val articleId: ArticleId
) {
    fun isAuthoredBy(authorId: AuthorId) = this.authorId == authorId
}

data class AuthorId private constructor(val value: UUID) {
    companion object {
        fun of(value: UUID) = AuthorId(value)
    }
}

data class CommentId private constructor(val value: UUID) {
    companion object {
        fun of(value: UUID) = CommentId(value)
    }
}

data class ArticleId private constructor(val value: UUID) {
    companion object {
        fun of(value: UUID) = ArticleId(value)
    }
}

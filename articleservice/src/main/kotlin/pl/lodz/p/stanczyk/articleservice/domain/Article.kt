package pl.lodz.p.stanczyk.articleservice.domain

import java.time.Instant
import java.util.UUID

data class Article(
    val id: ArticleId?,
    val title: String,
    val content: String,
    val publicationDate: Instant,
    val updatedAt: Instant?,
    val authorId: AuthorId
) {
    fun isAuthoredBy(authorId: AuthorId) = this.authorId == authorId
    fun patchWith(articleDraft: ArticleDraft, now: Instant) =
        this.copy(title = articleDraft.title, content = articleDraft.content, updatedAt = now)
}

data class ArticleId private constructor(val value: UUID) {
    companion object {
        fun of(id: UUID) = ArticleId(id)
    }
}

data class AuthorId private constructor(val value: UUID) {
    companion object {
        fun of(id: UUID) = AuthorId(id)
    }
}

data class ArticleDraft(
    val title: String,
    val content: String,
    val authorId: AuthorId,
)

package pl.lodz.p.stanczyk.gatewayservice.domain

import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import java.time.Instant
import java.util.UUID

data class Article(
    val id: UUID,
    val title: String,
    val content: String,
    val publicationDate: Instant,
    val author: Author
) {
    fun patchWithAuthor(author: Author) = this.copy(author = author)
}

data class ArticleList(
    val articles: List<Article>,
    val count: Int
)

data class ArticleDraft(
    val title: String,
    val content: String
)

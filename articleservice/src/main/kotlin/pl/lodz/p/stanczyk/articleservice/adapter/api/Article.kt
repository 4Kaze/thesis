package pl.lodz.p.stanczyk.articleservice.adapter.api

import pl.lodz.p.stanczyk.articleservice.domain.Article
import pl.lodz.p.stanczyk.articleservice.domain.ArticleDraft
import pl.lodz.p.stanczyk.articleservice.domain.AuthorId
import java.time.Instant
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ArticleRequest(
    @field:Size(min = 1, max = 50)
    @field:NotBlank
    val title: String,
    @field:Size(min = 1)
    @field:NotBlank
    val content: String,
) {
    fun toDomain(authorId: UUID) = ArticleDraft(
        title = this.title,
        content = this.content,
        authorId = AuthorId.of(authorId)
    )
}

data class ArticleResponse(
    val id: UUID,
    val title: String,
    val content: String,
    val publicationDate: Instant,
    val authorId: UUID,
) {
    companion object {
        fun fromDomain(article: Article) = ArticleResponse(
            id = article.id!!.value,
            title = article.title,
            content = article.content,
            publicationDate = article.publicationDate,
            authorId = article.authorId.value
        )
    }
}

data class ArticleListResponse(
    val articles: List<ArticleResponse>,
    val count: Int
) {
    companion object {
        fun fromDomain(articles: List<Article>) = ArticleListResponse(
            articles = articles.map(ArticleResponse::fromDomain),
            count = articles.size
        )
    }
}

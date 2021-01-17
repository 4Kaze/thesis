package pl.lodz.p.stanczyk.gatewayservice.adapter.api

import pl.lodz.p.stanczyk.gatewayservice.domain.Article
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleList
import java.time.Instant
import java.util.UUID

data class ArticleJsonRequest(
    val title: String,
    val content: String,
) {
    fun toDomain() = ArticleDraft(
        title = this.title,
        content = this.content
    )
}

data class ArticleJsonResponse(
    val id: UUID,
    val title: String,
    val content: String,
    val publicationDate: Instant,
    val author: AuthorJsonResponse,
) {
    companion object {
        fun from(article: Article) = ArticleJsonResponse(
            id = article.id,
            title = article.title,
            content = article.content,
            publicationDate = article.publicationDate,
            author = AuthorJsonResponse(
                id = article.author.id,
                name = article.author.name ?: "Unknown"
            )
        )
    }
}

data class AuthorJsonResponse(
    val id: UUID,
    val name: String
)

data class ArticleListJsonResponse(
    val articles: List<ArticleJsonResponse>,
    val count: Int
) {
    companion object {
        fun from(articleList: ArticleList) = ArticleListJsonResponse(
            articles = articleList.articles.map(ArticleJsonResponse::from),
            count = articleList.count
        )
    }
}

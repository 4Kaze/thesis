package pl.lodz.p.stanczyk.gatewayservice.adapter

import feign.FeignException
import pl.lodz.p.stanczyk.gatewayservice.domain.Article
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleList
import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import pl.lodz.p.stanczyk.gatewayservice.domain.article.port.ArticleProvider
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.ArticleClientResponse
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.ArticleDraftRequest
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.ArticleListClientResponse
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.ArticleServiceClient
import java.util.UUID

class ArticleServiceAdapter(private val articleServiceClient: ArticleServiceClient) : ArticleProvider {
    override fun findById(articleId: UUID): Article {
        try {
            return articleServiceClient.findArticle(articleId).toArticle()
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    override fun findLatest(page: Int?, pageSize: Int?): ArticleList {
        try {
            return articleServiceClient.getLatestArticles(page, pageSize).toArticleList()
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    override fun create(articleDraft: ArticleDraft): Article {
        try {
            return articleServiceClient.createArticle(articleDraft.toRequest()).toArticle()
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    override fun modify(articleId: UUID, articleDraft: ArticleDraft): Article {
        try {
            return articleServiceClient.modifyArticle(articleId, articleDraft.toRequest()).toArticle()
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    override fun delete(articleId: UUID) {
        try {
            return articleServiceClient.deleteArticle(articleId)
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    private fun ArticleClientResponse.toArticle() = Article(
        id = this.id,
        title = this.title,
        content = this.content,
        publicationDate = this.publicationDate,
        author = Author(
            id = this.authorId,
            name = null
        )
    )

    private fun ArticleListClientResponse.toArticleList() = ArticleList(
        articles = this.articles.map { it.toArticle() },
        count = this.count
    )

    private fun ArticleDraft.toRequest() = ArticleDraftRequest(
        title = title,
        content = content
    )
}
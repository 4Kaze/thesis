package pl.lodz.p.stanczyk.gatewayservice.domain.article

import pl.lodz.p.stanczyk.gatewayservice.domain.Article
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleList
import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import pl.lodz.p.stanczyk.gatewayservice.domain.article.port.ArticleProvider
import pl.lodz.p.stanczyk.gatewayservice.domain.user.User
import pl.lodz.p.stanczyk.gatewayservice.domain.user.port.UserProvider
import java.util.UUID

class ArticleService(private val articleProvider: ArticleProvider, private val userProvider: UserProvider) {
    fun getArticle(articleId: UUID): Article {
        val article = articleProvider.findById(articleId)
        val authorId = article.author.id
        val author = userProvider.findByIds(setOf(authorId))[authorId]?.toAuthor() ?: createUnknownAuthor(authorId)
        return article.patchWithAuthor(author)
    }

    fun getArticles(page: Int?, pageSize: Int?): ArticleList {
        val articleList = articleProvider.findLatest(page, pageSize)
        val authorIds = articleList.articles.map { it.author.id }.toSet()
        if (authorIds.isEmpty()) {
            return createEmptyArticleList()
        }
        val idsToUsers = userProvider.findByIds(authorIds)
        return ArticleList(
            articles = patchArticlesWithAuthorNames(articleList.articles, idsToUsers),
            count = articleList.count
        )
    }

    fun createArticle(articleDraft: ArticleDraft): Article = articleProvider.create(articleDraft)

    fun modifyArticle(articleId: UUID, articleDraft: ArticleDraft): Article = articleProvider.modify(articleId, articleDraft)

    fun delete(articleId: UUID) = articleProvider.delete(articleId)

    private fun createEmptyArticleList() = ArticleList(articles = emptyList(), count = 0)

    private fun patchArticlesWithAuthorNames(articles: List<Article>, idToName: Map<UUID, User>) =
        articles.map {
            val author = idToName[it.author.id]?.toAuthor() ?: createUnknownAuthor(it.author.id)
            it.patchWithAuthor(author)
        }

    private fun User.toAuthor() = Author(
        id = this.id,
        name = this.name
    )

    private fun createUnknownAuthor(authorId: UUID) = Author(
        id = authorId,
        name = "Unknown"
    )
}
package pl.lodz.p.stanczyk.articleservice.domain

import pl.lodz.p.stanczyk.articleservice.domain.port.ArticleRepository
import pl.lodz.p.stanczyk.articleservice.infrastructure.InstantNowSupplier

class ArticleService(private val articleRepository: ArticleRepository, private val instantNowSupplier: InstantNowSupplier) {
    fun createArticle(articleDraft: ArticleDraft): Article =
        articleRepository.save(articleDraft.toArticle())

    fun findById(articleId: ArticleId): Article? =
        articleRepository.findById(articleId)

    fun findLatest(offset: Int, count: Int): List<Article> =
        articleRepository.findLatest(offset, count)

    fun update(articleId: ArticleId, articleDraft: ArticleDraft): Article {
        val article = articleRepository.findById(articleId) ?: throw ArticleNotFoundException(articleId)
        validateAuthorship(article, articleDraft.authorId)
        val updatedArticle = article.patchWith(articleDraft, instantNowSupplier.get())
        return articleRepository.save(updatedArticle)
    }

    fun delete(articleId: ArticleId, authorId: AuthorId, force: Boolean) {
        val article = articleRepository.findById(articleId) ?: throw ArticleNotFoundException(articleId)
        if(!force) validateAuthorship(article, authorId)
        articleRepository.delete(articleId)
    }

    private fun validateAuthorship(article: Article, authorId: AuthorId) {
        if (!article.isAuthoredBy(authorId)) {
            throw AuthorshipException("User with id $authorId is not an author of an article with id ${article.id}")
        }
    }

    private fun ArticleDraft.toArticle() = Article(
        id = null,
        title = this.title,
        content = this.content,
        publicationDate = instantNowSupplier.get(),
        updatedAt = null,
        authorId = this.authorId
    )
}

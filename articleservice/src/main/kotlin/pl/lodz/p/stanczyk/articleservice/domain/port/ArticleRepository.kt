package pl.lodz.p.stanczyk.articleservice.domain.port

import pl.lodz.p.stanczyk.articleservice.domain.Article
import pl.lodz.p.stanczyk.articleservice.domain.ArticleId

interface ArticleRepository {
    fun save(article: Article): Article
    fun findById(articleId: ArticleId): Article?
    fun findLatest(page: Int, pageSize: Int): List<Article>
    fun delete(articleId: ArticleId)
}

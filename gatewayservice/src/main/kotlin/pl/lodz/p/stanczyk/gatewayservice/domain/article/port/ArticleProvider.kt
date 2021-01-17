package pl.lodz.p.stanczyk.gatewayservice.domain.article.port

import pl.lodz.p.stanczyk.gatewayservice.domain.Article
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.ArticleList
import java.util.UUID

interface ArticleProvider {
    fun findById(articleId: UUID): Article
    fun findLatest(page: Int?, pageSize: Int?): ArticleList
    fun create(articleDraft: ArticleDraft): Article
    fun modify(articleId: UUID, articleDraft: ArticleDraft): Article
    fun delete(articleId: UUID)
}
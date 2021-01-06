package pl.lodz.p.stanczyk.articleservice.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.MongoRepository
import pl.lodz.p.stanczyk.articleservice.domain.Article
import pl.lodz.p.stanczyk.articleservice.domain.ArticleId
import pl.lodz.p.stanczyk.articleservice.domain.AuthorId
import pl.lodz.p.stanczyk.articleservice.domain.port.ArticleRepository
import java.util.UUID
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article as ArticleEntity

class MongoArticleRepositoryAdapter(
    val mongoRepository: MongoRepository<ArticleEntity, String>
) : ArticleRepository {
    override fun save(article: Article): Article = mongoRepository.save(
        article.toEntity()
    ).toDomain()

    override fun findById(articleId: ArticleId): Article? = mongoRepository.findById(
        articleId.value.toString()
    ).orElse(null)?.toDomain()

    override fun findLatest(page: Int, pageSize: Int): List<Article> {
        val pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "publicationDate"))
        return mongoRepository.findAll(pageRequest).content.map { it.toDomain() }
    }

    override fun delete(articleId: ArticleId) = mongoRepository.deleteById(articleId.value.toString())

    private fun Article.toEntity() = ArticleEntity(
        id = this.id?.value?.toString() ?: UUID.randomUUID().toString(),
        title = this.title,
        content = this.content,
        publicationDate = this.publicationDate,
        updatedAt = this.updatedAt,
        authorId = this.authorId.value
    )

    private fun ArticleEntity.toDomain() = Article(
        id = createArticleId(this.id),
        title = this.title,
        content = this.content,
        publicationDate = this.publicationDate,
        updatedAt = this.updatedAt,
        authorId = AuthorId.of(this.authorId)
    )

    private fun createArticleId(id: String?) = id?.let {
        ArticleId.of(
            UUID.fromString(id)
        )
    } ?: throw IllegalArgumentException("Article id is null")
}

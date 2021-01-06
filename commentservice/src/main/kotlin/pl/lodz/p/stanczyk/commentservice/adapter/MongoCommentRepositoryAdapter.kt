package pl.lodz.p.stanczyk.commentservice.adapter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.lodz.p.stanczyk.commentservice.domain.ArticleId
import pl.lodz.p.stanczyk.commentservice.domain.AuthorId
import pl.lodz.p.stanczyk.commentservice.domain.Comment
import pl.lodz.p.stanczyk.commentservice.domain.CommentId
import pl.lodz.p.stanczyk.commentservice.domain.port.CommentRepository
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.MongoCommentRepository
import java.util.UUID
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.Comment as CommentEntity

class MongoCommentRepositoryAdapter(
    val mongoRepository: MongoCommentRepository
) : CommentRepository {
    override fun save(comment: Comment): Comment = mongoRepository.insert(
        comment.toEntity()
    ).toDomain()

    override fun findById(articleId: ArticleId, commentId: CommentId): Comment? =
        mongoRepository.findByArticleIdAndId(articleId.value, commentId.value.toString())?.toDomain()

    override fun delete(articleId: ArticleId, commentId: CommentId) =
        mongoRepository.deleteByArticleIdAndId(articleId.value, commentId.value.toString())

    override fun findByArticleId(articleId: ArticleId, page: Int, pageSize: Int): List<Comment> {
        val pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "publicationDate"))
        return mongoRepository.findByArticleId(articleId.value, pageRequest).content.map { it.toDomain() }
    }

    private fun Comment.toEntity() = CommentEntity(
        id = UUID.randomUUID().toString(),
        content = this.content,
        publicationDate = this.publicationDate,
        authorId = this.authorId.value,
        articleId = this.articleId.value
    )

    private fun CommentEntity.toDomain() = Comment(
        id = CommentId.of(UUID.fromString(this.id)),
        authorId = AuthorId.of(this.authorId),
        content = this.content,
        publicationDate = this.publicationDate,
        articleId = ArticleId.of(this.articleId)
    )
}

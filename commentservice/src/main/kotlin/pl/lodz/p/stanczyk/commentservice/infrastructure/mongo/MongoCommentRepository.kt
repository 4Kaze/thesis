package pl.lodz.p.stanczyk.commentservice.infrastructure.mongo

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MongoCommentRepository : MongoRepository<Comment, String> {
    fun findByArticleIdAndId(articleId: UUID, id: String): Comment?
    fun deleteByArticleIdAndId(articleId: UUID, id: String)
    fun findByArticleId(articleId: UUID, pageable: Pageable): Page<Comment>
}

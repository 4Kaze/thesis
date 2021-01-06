package pl.lodz.p.stanczyk.commentservice.domain.port

import pl.lodz.p.stanczyk.commentservice.domain.ArticleId
import pl.lodz.p.stanczyk.commentservice.domain.Comment
import pl.lodz.p.stanczyk.commentservice.domain.CommentId

interface CommentRepository {
    fun save(comment: Comment): Comment
    fun findById(articleId: ArticleId, commentId: CommentId): Comment?
    fun findByArticleId(articleId: ArticleId, page: Int, pageSize: Int): List<Comment>
    fun delete(articleId: ArticleId, commentId: CommentId)
}

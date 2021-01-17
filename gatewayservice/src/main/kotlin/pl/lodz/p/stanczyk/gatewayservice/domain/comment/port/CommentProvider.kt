package pl.lodz.p.stanczyk.gatewayservice.domain.comment.port

import pl.lodz.p.stanczyk.gatewayservice.domain.comment.Comment
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentList
import java.util.UUID

interface CommentProvider {
    fun find(articleId: UUID, page: Int?, pageSize: Int?): CommentList
    fun add(articleId: UUID, commentDraft: CommentDraft): Comment
    fun delete(articleId: UUID, commentId: UUID)
}
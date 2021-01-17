package pl.lodz.p.stanczyk.gatewayservice.adapter

import feign.FeignException
import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.Comment
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentList
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.port.CommentProvider
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.CommentClientResponse
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.CommentListClientResponse
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.CommentRequest
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.CommentServiceClient
import java.util.UUID

class CommentServiceAdapter(private val commentServiceClient: CommentServiceClient) : CommentProvider {
    override fun find(articleId: UUID, page: Int?, pageSize: Int?): CommentList {
        try {
            return commentServiceClient.getComments(articleId, page, pageSize).toCommentList()
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    override fun add(articleId: UUID, commentDraft: CommentDraft): Comment {
        try {
            return commentServiceClient.addComment(articleId, commentDraft.toRequest()).toComment()
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    override fun delete(articleId: UUID, commentId: UUID) {
        try {
            return commentServiceClient.deleteComment(articleId, commentId)
        } catch (exception: FeignException) {
            throw ServiceClientException(exception.contentUTF8(), exception.status())
        }
    }

    private fun CommentDraft.toRequest() = CommentRequest(
        content = this.content
    )

    private fun CommentClientResponse.toComment() = Comment(
        id = this.id,
        content = this.content,
        publicationDate = this.publicationTime,
        author = Author(
            id = this.authorId,
            name = null
        )
    )

    private fun CommentListClientResponse.toCommentList() = CommentList(
        comments = this.comments.map { it.toComment() },
        count = this.count
    )
}
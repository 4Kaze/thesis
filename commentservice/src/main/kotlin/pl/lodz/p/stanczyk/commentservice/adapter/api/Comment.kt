package pl.lodz.p.stanczyk.commentservice.adapter.api

import pl.lodz.p.stanczyk.commentservice.domain.ArticleId
import pl.lodz.p.stanczyk.commentservice.domain.AuthorId
import pl.lodz.p.stanczyk.commentservice.domain.Comment
import pl.lodz.p.stanczyk.commentservice.domain.CommentDraft
import java.time.Instant
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CommentRequest(
    @field:Size(min = 1, max = 500)
    @field:NotBlank
    val content: String
) {
    fun toDraft(authorId: UUID, articleId: UUID) = CommentDraft(
        authorId = AuthorId.of(authorId),
        articleId = ArticleId.of(articleId),
        content = content
    )
}

data class CommentResponse(
    val id: UUID,
    val content: String,
    val publicationTime: Instant,
    val authorId: UUID,
) {
    companion object {
        fun fromDomain(comment: Comment) = CommentResponse(
            id = comment.id!!.value,
            content = comment.content,
            publicationTime = comment.publicationDate,
            authorId = comment.authorId.value
        )
    }
}

data class CommentListResponse(
    val comments: List<CommentResponse>,
    val count: Int
) {
    companion object {
        fun fromDomain(comments: List<Comment>) = CommentListResponse(
            comments = comments.map(CommentResponse::fromDomain),
            count = comments.size
        )
    }
}

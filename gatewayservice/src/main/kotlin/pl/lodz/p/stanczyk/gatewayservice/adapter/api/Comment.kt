import pl.lodz.p.stanczyk.gatewayservice.domain.comment.Comment
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentDraft
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentList
import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import java.time.Instant
import java.util.UUID

data class CommentJsonRequest(
    val content: String
) {
    fun toDraft(articleId: UUID) = CommentDraft(
        articleId = articleId,
        content = content
    )
}

data class CommentJsonResponse(
    val id: UUID,
    val content: String,
    val publicationTime: Instant,
    val author: AuthorJsonResponse,
) {
    companion object {
        fun from(comment: Comment) = CommentJsonResponse(
            id = comment.id,
            content = comment.content,
            publicationTime = comment.publicationDate,
            author = AuthorJsonResponse.from(comment.author)
        )
    }
}

data class AuthorJsonResponse(
    val id: UUID,
    val name: String
) {
    companion object {
        fun from(author: Author) = AuthorJsonResponse(
            id = author.id,
            name = author.name ?: "Unknown"
        )
    }
}

data class CommentListJsonResponse(
    val comments: List<CommentJsonResponse>,
    val count: Int
) {
    companion object {
        fun from(commentList: CommentList) = CommentListJsonResponse(
            comments = commentList.comments.map(CommentJsonResponse::from),
            count = commentList.count
        )
    }
}

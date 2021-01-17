package pl.lodz.p.stanczyk.gatewayservice.domain.comment

import pl.lodz.p.stanczyk.gatewayservice.domain.article.port.ArticleProvider
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.port.CommentProvider
import pl.lodz.p.stanczyk.gatewayservice.domain.shared.Author
import pl.lodz.p.stanczyk.gatewayservice.domain.user.User
import pl.lodz.p.stanczyk.gatewayservice.domain.user.port.UserProvider
import java.util.UUID

class CommentService(
    private val articleProvider: ArticleProvider,
    private val commentProvider: CommentProvider,
    private val userProvider: UserProvider
) {
    fun getComments(articleId: UUID, page: Int?, pageSize: Int?): CommentList {
        val commentList = commentProvider.find(articleId, page, pageSize)
        val userIds = commentList.comments.map { it.author.id }.toSet()
        if (userIds.isEmpty()) {
            return createEmptyCommentList()
        }
        val idsToUsers = userProvider.findByIds(userIds)
        return CommentList(
            comments = patchCommentsWithAuthorNames(commentList.comments, idsToUsers),
            count = commentList.count
        )
    }

    fun addComment(articleId: UUID, commentDraft: CommentDraft): Comment =
        articleProvider.findById(articleId).let {
            commentProvider.add(articleId, commentDraft)
        }

    fun patchCommentsWithAuthorNames(comments: List<Comment>, idsToUsers: Map<UUID, User>) =
        comments.map {
            val author = idsToUsers[it.author.id]?.toAuthor() ?: createUnknownAuthor(it.author.id)
            it.patchWithAuthor(author)
        }

    private fun createEmptyCommentList() = CommentList(comments = emptyList(), count = 0)

    private fun User.toAuthor() = Author(
        id = this.id,
        name = this.name
    )

    private fun createUnknownAuthor(authorId: UUID) = Author(
        id = authorId,
        name = "Unknown"
    )

    fun deleteComment(articleId: UUID, commentId: UUID) = commentProvider.delete(articleId, commentId)

}
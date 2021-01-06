package pl.lodz.p.stanczyk.commentservice.domain

import pl.lodz.p.stanczyk.commentservice.domain.port.CommentRepository
import pl.lodz.p.stanczyk.commentservice.infrastructure.InstantNowSupplier

class CommentService(private val commentRepository: CommentRepository, private val instantNowSupplier: InstantNowSupplier) {
    fun createComment(commentDraft: CommentDraft): Comment =
        commentRepository.save(commentDraft.toComment())

    fun deleteComment(articleId: ArticleId, commentId: CommentId, authorId: AuthorId, force: Boolean) {
        val comment = commentRepository.findById(articleId, commentId) ?: throw CommentNotFoundException(articleId, commentId)
        if (!force) validateAuthorship(comment, authorId)
        commentRepository.delete(articleId, commentId)
    }

    fun findForArticle(articleId: ArticleId, page: Int, pageSize: Int) =
        commentRepository.findByArticleId(articleId, page, pageSize)

    private fun validateAuthorship(comment: Comment, authorId: AuthorId) {
        if (!comment.isAuthoredBy(authorId)) {
            throw AuthorshipException("User with id $authorId is not an author of a comment with id ${comment.id} in an article with id ${comment.articleId}")
        }
    }

    private fun CommentDraft.toComment() = Comment(
        id = null,
        authorId = this.authorId,
        content = this.content,
        publicationDate = instantNowSupplier.get(),
        articleId = this.articleId
    )
}

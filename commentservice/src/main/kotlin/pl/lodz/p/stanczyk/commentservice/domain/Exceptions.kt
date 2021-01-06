package pl.lodz.p.stanczyk.commentservice.domain

class AuthorshipException(message: String) : Exception(message)
class CommentNotFoundException(articleId: ArticleId, commentId: CommentId) : Exception("A comment with id $commentId for an article with id $articleId was not found")

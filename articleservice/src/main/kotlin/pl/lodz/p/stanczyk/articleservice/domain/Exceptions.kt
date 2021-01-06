package pl.lodz.p.stanczyk.articleservice.domain

class AuthorshipException(message: String): Exception(message)
class ArticleNotFoundException(articleId: ArticleId): Exception("An article with id $articleId was not found")
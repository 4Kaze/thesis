package pl.lodz.p.stanczyk.articleservice.adapter.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.lodz.p.stanczyk.articleservice.domain.ArticleNotFoundException
import pl.lodz.p.stanczyk.articleservice.domain.AuthorshipException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(ConstraintViolationException::class)
    private fun handleConstraintViolation(exception: ConstraintViolationException): ResponseEntity<Error> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Error(exception.constraintViolations.joinToString(", "))
        )

    @ExceptionHandler(ArticleNotFoundException::class)
    private fun handleArticleNotFound(exception: ArticleNotFoundException): ResponseEntity<Error> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Error(exception.message ?: "")
        )

    @ExceptionHandler(AuthorshipException::class)
    private fun handleAuthorshipViolation(exception: AuthorshipException): ResponseEntity<Error> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            Error(exception.message ?: "")
        )

    data class Error(val message: String)
}

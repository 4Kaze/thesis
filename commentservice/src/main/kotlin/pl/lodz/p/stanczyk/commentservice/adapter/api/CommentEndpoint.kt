package pl.lodz.p.stanczyk.commentservice.adapter.api

import org.keycloak.KeycloakPrincipal
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.lodz.p.stanczyk.commentservice.domain.ArticleId
import pl.lodz.p.stanczyk.commentservice.domain.AuthorId
import pl.lodz.p.stanczyk.commentservice.domain.CommentId
import pl.lodz.p.stanczyk.commentservice.domain.CommentService
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@Validated
class CommentEndpoint(val commentService: CommentService) {
    @PostMapping(
        path = ["/articles/{articleId}/comments"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addComment(
        @PathVariable articleId: UUID,
        @Valid @RequestBody request: CommentRequest,
        authentication: Authentication
    ): CommentResponse {
        val createdComment = commentService.createComment(request.toDraft(authentication.getUserId(), articleId))
        return CommentResponse.fromDomain(createdComment)
    }

    @DeleteMapping(
        path = ["/articles/{articleId}/comments/{commentId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addComment(
        @PathVariable articleId: UUID,
        @PathVariable commentId: UUID,
        authentication: Authentication
    ) = commentService.deleteComment(
        ArticleId.of(articleId),
        CommentId.of(commentId),
        AuthorId.of(authentication.getUserId()),
        authentication.isAdmin()
    )

    @GetMapping(
        path = ["/articles/{articleId}/comments"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getComments(
        @PathVariable articleId: UUID,
        @RequestParam(defaultValue = "1") @Min(1) page: Int,
        @RequestParam(defaultValue = "10") @Max(50) @Min(1) pageSize: Int
    ): CommentListResponse = CommentListResponse.fromDomain(
        commentService.findForArticle(ArticleId.of(articleId), page, pageSize)
    )

    private fun Authentication.getUserId(): UUID {
        return with(this.principal as KeycloakPrincipal<*>) {
            this.keycloakSecurityContext.token.subject?.let { UUID.fromString(it) }
                ?: throw IllegalArgumentException("No subject provided in token")
        }
    }

    private fun Authentication.isAdmin(): Boolean {
        return with(this.details as SimpleKeycloakAccount) {
            "admin" in this.roles
        }
    }
}

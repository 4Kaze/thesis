package pl.lodz.p.stanczyk.articleservice.adapter.api

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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pl.lodz.p.stanczyk.articleservice.domain.ArticleId
import pl.lodz.p.stanczyk.articleservice.domain.ArticleService
import pl.lodz.p.stanczyk.articleservice.domain.AuthorId
import java.lang.IllegalArgumentException
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@Validated
class ArticleEndpoint(val articleService: ArticleService) {
    @PostMapping(
        path = ["/articles"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createArticle(
        @Valid @RequestBody request: ArticleRequest,
        authentication: Authentication
    ): ArticleResponse {
        val createdArticle = articleService.createArticle(request.toDomain(authentication.getUserId()))
        return ArticleResponse.fromDomain(createdArticle)
    }

    @GetMapping(
        path = ["/articles/{articleId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getArticle(@PathVariable articleId: UUID): ArticleResponse {
        val article = articleService.findById(ArticleId.of(articleId))
        return article?.let { ArticleResponse.fromDomain(article) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "An article with id $articleId does not exist.")
    }

    @GetMapping(
        path = ["/articles"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getArticles(
        @RequestParam(defaultValue = "1") @Min(1) page: Int,
        @RequestParam(defaultValue = "10") @Max(50) @Min(1) pageSize: Int
    ): ArticleListResponse = ArticleListResponse.fromDomain(articleService.findLatest(page, pageSize))

    @PutMapping(
        path = ["/articles/{articleId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun modifyArticle(
        @PathVariable articleId: UUID,
        @Valid @RequestBody request: ArticleRequest,
        authentication: Authentication
    ): ArticleResponse {
        val updatedArticle = articleService.update(ArticleId.of(articleId), request.toDomain(authentication.getUserId()))
        return ArticleResponse.fromDomain(updatedArticle)
    }

    @DeleteMapping(
        path = ["/articles/{articleId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArticle(@PathVariable articleId: UUID, authentication: Authentication) {
        articleService.delete(ArticleId.of(articleId), AuthorId.of(authentication.getUserId()), authentication.isAdmin())
    }

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

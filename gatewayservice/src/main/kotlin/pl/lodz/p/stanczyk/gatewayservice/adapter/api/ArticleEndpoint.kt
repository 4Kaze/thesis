package pl.lodz.p.stanczyk.gatewayservice.adapter.api

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
import pl.lodz.p.stanczyk.gatewayservice.domain.article.ArticleService
import java.util.UUID
import javax.validation.Valid

@RestController
@Validated
class ArticleEndpoint(private val articleService: ArticleService) {
    @GetMapping(
        path = ["/articles/{articleId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getArticle(@PathVariable articleId: UUID): ArticleJsonResponse =
        ArticleJsonResponse.from(articleService.getArticle(articleId))

    @GetMapping(
        path = ["/articles"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getArticle(@RequestParam page: Int?, @RequestParam pageSize: Int?): ArticleListJsonResponse =
        ArticleListJsonResponse.from(articleService.getArticles(page, pageSize))

    @PostMapping(
        path = ["/articles"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createArticle(@RequestBody request: ArticleJsonRequest) = articleService.createArticle(request.toDomain())

    @PutMapping(
        path = ["/articles/{articleId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun modifyArticle(
        @PathVariable articleId: UUID,
        @Valid @RequestBody request: ArticleJsonRequest,
    ) = articleService.modifyArticle(articleId, request.toDomain())

    @DeleteMapping(
        path = ["/articles/{articleId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArticle(@PathVariable articleId: UUID) = articleService.delete(articleId)
}
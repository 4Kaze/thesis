package pl.lodz.p.stanczyk.gatewayservice.infrastructure.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.Instant
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@FeignClient(name = "ArticleService", url = "\${services.article.url}")
interface ArticleServiceClient {
    @GetMapping(
        path = ["/articles/{articleId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun findArticle(@PathVariable articleId: UUID): ArticleClientResponse

    @GetMapping(
        path = ["/articles"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getLatestArticles(
        @RequestParam page: Int?,
        @RequestParam pageSize: Int?
    ): ArticleListClientResponse

    @PostMapping(
        path = ["/articles"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createArticle(@Valid @RequestBody request: ArticleDraftRequest): ArticleClientResponse

    @PutMapping(
        path = ["/articles/{articleId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun modifyArticle(
        @PathVariable articleId: UUID,
        @Valid @RequestBody request: ArticleDraftRequest
    ): ArticleClientResponse

    @DeleteMapping(
        path = ["/articles/{articleId}"]
    )
    fun deleteArticle(@PathVariable articleId: UUID)
}

data class ArticleDraftRequest(
    val title: String,
    val content: String
)

data class ArticleClientResponse(
    val id: UUID,
    val title: String,
    val content: String,
    val publicationDate: Instant,
    val authorId: UUID,
)

data class ArticleListClientResponse(
    val articles: List<ArticleClientResponse>,
    val count: Int
)
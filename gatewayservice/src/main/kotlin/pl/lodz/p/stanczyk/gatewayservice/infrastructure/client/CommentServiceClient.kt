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

@FeignClient(name = "CommentService", url = "\${services.comment.url}")
interface CommentServiceClient {
    @GetMapping(
        path = ["/articles/{articleId}/comments"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getComments(
        @PathVariable articleId: UUID,
        @RequestParam(defaultValue = "1") page: Int?,
        @RequestParam(defaultValue = "10") pageSize: Int?
    ): CommentListClientResponse

    @PostMapping(
        path = ["/articles/{articleId}/comments"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addComment(
        @PathVariable articleId: UUID,
        @Valid @RequestBody request: CommentRequest
    ): CommentClientResponse

    @DeleteMapping(
        path = ["/articles/{articleId}/comments/{commentId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable articleId: UUID,
        @PathVariable commentId: UUID
    )
}

data class CommentRequest(
    val content: String
)

data class CommentClientResponse(
    val id: UUID,
    val content: String,
    val publicationTime: Instant,
    val authorId: UUID,
)

data class CommentListClientResponse(
    val comments: List<CommentClientResponse>,
    val count: Int
)
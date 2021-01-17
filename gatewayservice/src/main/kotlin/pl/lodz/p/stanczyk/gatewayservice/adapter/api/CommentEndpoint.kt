package pl.lodz.p.stanczyk.gatewayservice.adapter.api

import CommentJsonRequest
import CommentJsonResponse
import CommentListJsonResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentService
import java.util.UUID
import javax.validation.Valid

@RestController
@Validated
class CommentEndpoint(private val commentService: CommentService) {
    @GetMapping(
        path = ["/articles/{articleId}/comments"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getComments(
        @PathVariable articleId: UUID,
        @RequestParam page: Int?,
        @RequestParam pageSize: Int?
    ): CommentListJsonResponse =
        CommentListJsonResponse.from(commentService.getComments(articleId, page, pageSize))

    @PostMapping(
        path = ["/articles/{articleId}/comments"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addComment(
        @PathVariable articleId: UUID,
        @Valid @RequestBody request: CommentJsonRequest
    ): CommentJsonResponse =
        CommentJsonResponse.from(commentService.addComment(articleId, request.toDraft(articleId)))

    @DeleteMapping(
        path = ["/articles/{articleId}/comments/{commentId}"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable articleId: UUID,
        @PathVariable commentId: UUID
    ) = commentService.deleteComment(articleId, commentId)
}
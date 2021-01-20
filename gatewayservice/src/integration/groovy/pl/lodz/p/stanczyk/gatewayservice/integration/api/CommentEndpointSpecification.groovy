package pl.lodz.p.stanczyk.gatewayservice.integration.api

import org.keycloak.authorization.client.util.Http
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec
import pl.lodz.p.stanczyk.gatewayservice.integration.oauth.BearerTokenCreator
import pl.lodz.p.stanczyk.gatewayservice.integration.stub.ArticleServiceStub
import pl.lodz.p.stanczyk.gatewayservice.integration.stub.CommentServiceStub
import pl.lodz.p.stanczyk.gatewayservice.integration.stub.UserServiceStub

import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.ArticleBuilder.anArticle
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.CommentBuilder.aComment
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.CommentDraftBuilder.aCommentDraft
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.CommentListBuilder.aCommentsList
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.UserBuilder.aUser

class CommentEndpointSpecification extends IntegrationSpec implements CommentServiceStub, ArticleServiceStub, UserServiceStub, BearerTokenCreator {
    static final def ARTICLE_ID_1 = "569d67ec-5565-4a71-a7ec-abfbcd82dfac"
    static final def COMMENT_ID_1 = "0e41039b-1e8e-4853-9244-12cef55694c1"
    static final def COMMENT_ID_2 = "6d617ac6-c694-4f1a-8916-7742b0f98f92"
    static final def AUTHOR_ID_1 = "32b6abf2-ff57-451b-8561-a15a02258c2f"
    static final def AUTHOR_ID_2 = "73dee293-446f-4629-88a3-02e377dcd581"

    def USER_READER_TOKEN

    def setup() {
        USER_READER_TOKEN = oAuthTokenFor(AUTHOR_ID_1.toString(), "username", "name", "reader")
    }

    def "should fetch a list of articles and enrich them with authors' names"() {
        given:
        def comment1 = aComment().withId(COMMENT_ID_1).withAuthorId(AUTHOR_ID_1)
        def comment2 = aComment().withId(COMMENT_ID_2).withAuthorId(AUTHOR_ID_2)
        stubGetArticle(ARTICLE_ID_1, anArticle().build(), 200)
        stubGetComments(ARTICLE_ID_1, 1, 10, aCommentsList().withComments([comment1, comment2]).build())
        stubGetUsers([AUTHOR_ID_1, AUTHOR_ID_2], [aUser(AUTHOR_ID_1, "Author 1"), aUser(AUTHOR_ID_2, "Author 2")])

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 1, 10)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body.comments[0]) {
            id == COMMENT_ID_1
            author.id == AUTHOR_ID_1
            author.name == "Author 1"
        }
        with(response.body.comments[1]) {
            id == COMMENT_ID_2
            author.id == AUTHOR_ID_2
            author.name == "Author 2"
        }
    }

    def "should not proxy an add comment request when an article does not exist"() {
        given:
        def comment = aCommentDraft().build()
        stubGetArticle(ARTICLE_ID_1, null, 404)

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID_1, comment, USER_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should proxy an add comment request"() {
        given:
        def commentDraft = aCommentDraft().build()
        def commentResponse = aComment().withId(COMMENT_ID_1).build()
        stubGetArticle(ARTICLE_ID_1, anArticle().build(), 200)
        stubAddComment(ARTICLE_ID_1, commentDraft, commentResponse, USER_READER_TOKEN)

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID_1, commentDraft, USER_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
        response.body.id == COMMENT_ID_1
    }

    def "should proxy a delete comment request"() {
        given:
        stubDeleteComment(ARTICLE_ID_1, COMMENT_ID_1, USER_READER_TOKEN, 204)

        when:
        def response = deleteCommentRequestIsSent(ARTICLE_ID_1, COMMENT_ID_1, USER_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should pass an error response from comment service"() {
        given:
        stubGetComments(ARTICLE_ID_1, 1, 10, ["message": "some error message"], 404)

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 1, 10)

        then:
        response.statusCodeValue == 404
        response.body.message == "some error message"
    }

    def "should not fail when userservice responds with an error"() {
        given:
        def comment1 = aComment().withId(COMMENT_ID_1).withAuthorId(AUTHOR_ID_1)
        stubGetArticle(ARTICLE_ID_1, anArticle().build(), 200)
        stubGetComments(ARTICLE_ID_1, 1, 10, aCommentsList().withComments([comment1]).build())
        stubGetUsers([AUTHOR_ID_1], ["message": "some error"], 500)

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 1, 10)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body.comments[0]) {
            id == COMMENT_ID_1
            author.id == AUTHOR_ID_1
            author.name == null
        }
    }

    ResponseEntity<Map> getCommentsRequestIsSent(String articleId, int page, int pageSize) {
        restTemplate.getForEntity(url("/articles/$articleId/comments?page=$page&pageSize=$pageSize"), Map)
    }

    ResponseEntity<Map> addCommentRequestIsSent(String articleId, Map request, String token) {
        def headers = new HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        def entity = new HttpEntity(request, headers)
        restTemplate.postForEntity(url("/articles/$articleId/comments"), entity, Map)
    }

    ResponseEntity<Map> deleteCommentRequestIsSent(String articleId, String commentId, String token) {
        def headers = new HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        def entity = new HttpEntity(null, headers)
        restTemplate.exchange(url("/articles/$articleId/comments/$commentId"), HttpMethod.DELETE, entity, Map) as ResponseEntity<Map>
    }
}

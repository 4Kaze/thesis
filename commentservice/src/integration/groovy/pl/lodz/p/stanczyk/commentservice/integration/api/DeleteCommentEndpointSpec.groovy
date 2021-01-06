package pl.lodz.p.stanczyk.commentservice.integration.api

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.Comment
import pl.lodz.p.stanczyk.commentservice.integration.IntegrationSpec
import pl.lodz.p.stanczyk.commentservice.integration.oauth.BearerTokenCreator
import spock.lang.Unroll

import java.time.Instant

import static pl.lodz.p.stanczyk.commentservice.integration.api.CommentBuilder.aComment
import static pl.lodz.p.stanczyk.commentservice.integration.api.CommentRequestBuilder.aCommentRequest

class DeleteCommentEndpointSpec extends IntegrationSpec implements BearerTokenCreator {
    static final def USER_ID_1 = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def USER_ID_2 = UUID.fromString("219ebe1c-9610-4c84-bf98-bb7dc4b1d62b")
    static final def COMMENT_ID = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    static final def ARTICLE_ID = UUID.fromString("996a2d25-c74c-43f3-afbe-47e3cdb264c2")
    static final def SOME_UUID = UUID.fromString("dac63298-d2e8-4015-bef7-55221e4fd5a9")
    def USER_1_READER_TOKEN

    def setup() {
        USER_1_READER_TOKEN = oAuthTokenFor(USER_ID_1.toString(), "Username", "Name", "reader")
    }

    def "should remove a comment and return 204"() {
        given:
        thereIsASavedComment(
                aComment()
                        .withId(COMMENT_ID)
                        .withAuthorId(USER_ID_1)
                        .withArticleId(ARTICLE_ID)
                        .build()
        )

        when:
        def response = deleteCommentRequestIsSent(ARTICLE_ID, COMMENT_ID, USER_1_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.NO_CONTENT

        and:
        assertNoCommentsAreStored()
    }

    def "should remove a comment when requester is not an author of the comment, but has an admin role"() {
        given:
        thereIsASavedComment(
                aComment()
                        .withId(COMMENT_ID)
                        .withAuthorId(USER_ID_1)
                        .withArticleId(ARTICLE_ID)
                        .build()
        )

        and:
        def adminToken = oAuthTokenFor(USER_ID_2.toString(), "Username", "Name", "admin")

        when:
        def response = deleteCommentRequestIsSent(ARTICLE_ID, COMMENT_ID, adminToken)

        then:
        response.statusCode == HttpStatus.NO_CONTENT

        and:
        assertNoCommentsAreStored()
    }

    def "should return 403 when user is not an author of the comment"() {
        given:
        thereIsASavedComment(
                aComment()
                        .withId(COMMENT_ID)
                        .withAuthorId(USER_ID_2)
                        .withArticleId(ARTICLE_ID)
                        .build()
        )

        when:
        def response = deleteCommentRequestIsSent(ARTICLE_ID, COMMENT_ID, USER_1_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }

    def "should return 401 when no auth token is provided"() {
        when:
        def response = deleteCommentRequestIsSent(ARTICLE_ID, COMMENT_ID, null)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    @Unroll
    def "should return 404 when #desc any stored comment"() {
        given:
        thereIsASavedComment(
                aComment()
                        .withId(COMMENT_ID)
                        .withAuthorId(USER_ID_1)
                        .withArticleId(ARTICLE_ID)
                        .build()
        )

        when:
        def response = deleteCommentRequestIsSent(requestedArticleId, requestedCommentId, USER_1_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.NOT_FOUND

        where:
        requestedArticleId | requestedCommentId | desc
        SOME_UUID          | COMMENT_ID         | "article id does not match"
        ARTICLE_ID         | SOME_UUID          | "comment id does not match"
        SOME_UUID          | SOME_UUID          | "comment and article id does not match"
    }

    ResponseEntity<Map> deleteCommentRequestIsSent(UUID articleId, UUID commentId, String token) {
        def headers = new HttpHeaders()
        if (token != null) {
            headers.add("Authorization", "Bearer $token")
        }
        def entity = new HttpEntity(null, headers)
        restTemplate.exchange(url("/articles/$articleId/comments/$commentId"), HttpMethod.DELETE, entity, Map)
    }

    void thereIsASavedComment(Comment comment) {
        mongoTemplate.insert(comment)
    }

    void assertNoCommentsAreStored() {
        assert mongoTemplate.getCollection(mongoTemplate.getCollectionName(Comment)).countDocuments() == 0
    }
}

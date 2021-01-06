package pl.lodz.p.stanczyk.commentservice.integration.api

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.Comment
import pl.lodz.p.stanczyk.commentservice.integration.IntegrationSpec
import pl.lodz.p.stanczyk.commentservice.integration.oauth.BearerTokenCreator
import spock.lang.Unroll

import java.time.Instant

import static pl.lodz.p.stanczyk.commentservice.integration.api.CommentRequestBuilder.aCommentRequest

class AddCommentEndpointSpec extends IntegrationSpec implements BearerTokenCreator {
    static final def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def ARTICLE_ID = "996a2d25-c74c-43f3-afbe-47e3cdb264c2"
    def USER_READER_TOKEN

    def setup() {
        USER_READER_TOKEN = oAuthTokenFor(USER_ID.toString(), "Username", "Name", "reader")
    }

    def "should return added comment"() {
        given:
        def request = aCommentRequest().withContent("Comment content").build()

        and:
        def currentTime = nowInMillis()
        theTimeIs(currentTime)

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID, request, USER_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body) {
            id != null
            content == "Comment content"
            Instant.parse(publicationTime) == currentTime
            authorId == USER_ID.toString()
        }
    }

    def "should save comment in database"() {
        given:
        def request = aCommentRequest().withContent("Comment content").build()

        and:
        def currentTime = nowInMillis()
        theTimeIs(currentTime)

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID, request, USER_READER_TOKEN)

        then:
        with(savedComment(response.body.id)) {
            content == "Comment content"
            publicationDate == currentTime
            authorId == USER_ID
            articleId == UUID.fromString(ARTICLE_ID)
        }
    }

    def "should return 200 when content is 500 characters long"() {
        given:
        def request = aCommentRequest().withContent("1" * 500).build()

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID, request, USER_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
    }

    @Unroll
    def "should return 400 when content #desc"() {
        given:
        def request = aCommentRequest().withContent(content).build()

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID, request, USER_READER_TOKEN)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        assertNoCommentsAreStored()

        where:
        content   | desc
        " "       | "is blank"
        ""        | "is empty"
        null      | "is null"
        "1" * 501 | "exceeds 500 characters"
    }

    def "should return 403 when user does not have a reader role"() {
        given:
        def request = aCommentRequest().build()

        and:
        def onlyWriterToken = oAuthTokenFor(USER_ID.toString(), "Username", "name", "writer")

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID, request, onlyWriterToken)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }

    def "should return 401 when no auth token is provided"() {
        given:
        def request = aCommentRequest().build()

        when:
        def response = addCommentRequestIsSent(ARTICLE_ID, request, null)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    ResponseEntity<Map> addCommentRequestIsSent(String articleId, Map request, String token) {
        def headers = new HttpHeaders()
        if (token != null) {
            headers.add("Authorization", "Bearer $token")
        }
        def entity = new HttpEntity(request, headers)
        restTemplate.postForEntity(url("/articles/$articleId/comments"), entity, Map)
    }

    Comment savedComment(String commentId) {
        mongoTemplate.findById(commentId, Comment)
    }

    void assertNoCommentsAreStored() {
        assert mongoTemplate.getCollection(mongoTemplate.getCollectionName(Comment)).countDocuments() == 0
    }
}

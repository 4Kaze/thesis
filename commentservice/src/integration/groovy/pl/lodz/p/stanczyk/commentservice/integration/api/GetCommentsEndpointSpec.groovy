package pl.lodz.p.stanczyk.commentservice.integration.api

import org.springframework.http.*
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.Comment
import pl.lodz.p.stanczyk.commentservice.integration.IntegrationSpec
import pl.lodz.p.stanczyk.commentservice.integration.oauth.BearerTokenCreator
import spock.lang.Unroll

import java.time.Instant

import static pl.lodz.p.stanczyk.commentservice.integration.api.CommentBuilder.aComment

class GetCommentsEndpointSpec extends IntegrationSpec implements BearerTokenCreator {
    static final def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def ARTICLE_ID_1 = UUID.fromString("996a2d25-c74c-43f3-afbe-47e3cdb264c2")
    static final def ARTICLE_ID_2 = UUID.fromString("78941a5f-1a3b-4eb4-a427-a7c899690b1c")
    static final def COMMENT_ID_1 = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    static final def COMMENT_ID_2 = UUID.fromString("6d617ac6-c694-4f1a-8916-7742b0f98f92")
    static final def COMMENT_ID_3 = UUID.fromString("0e41039b-1e8e-4853-9244-12cef55694c1")
    static final def COMMENT_ID_4 = UUID.fromString("bfde8f3c-00eb-4489-975c-0054941de2be")
    static final def COMMENT_ID_5 = UUID.fromString("b98cb5b2-542a-4db0-9419-1a36685810e7")
    static final def PUBLICATION_DATE_1 = Instant.parse("2007-12-03T10:00:00.00Z")
    static final def PUBLICATION_DATE_2 = Instant.parse("2007-12-03T10:01:00.00Z")
    static final def PUBLICATION_DATE_3 = Instant.parse("2007-12-03T10:10:00.00Z")
    static final def PUBLICATION_DATE_4 = Instant.parse("2007-12-03T10:11:00.00Z")
    static final def PUBLICATION_DATE_5 = Instant.parse("2007-12-03T10:12:00.00Z")

    def "should return stored comments"() {
        given:
        thereIsASavedComment(
                aComment()
                        .withId(COMMENT_ID_1)
                        .withArticleId(ARTICLE_ID_1)
                        .withPublicationDate(PUBLICATION_DATE_1)
                        .withAuthorId(USER_ID)
                        .withContent("Some content")
                        .build()
        )

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1)

        then:
        response.statusCode == HttpStatus.OK
        response.body.count == 1
        response.body.comments.size() == 1
        with(response.body.comments[0]) {
            id == COMMENT_ID_1.toString()
            Instant.parse(publicationTime) == PUBLICATION_DATE_1
            authorId == USER_ID.toString()
            content == "Some content"
        }
    }

    def "should return comments for an article sorted by publication time"() {
        given:
        thereIsASavedComment(aComment().withId(COMMENT_ID_1).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_2).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_2).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_1).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_3).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_3).build())

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1)

        then:
        response.statusCode == HttpStatus.OK
        response.body.count == 3
        response.body.comments.size() == 3
        response.body.comments[0].id == COMMENT_ID_3.toString()
        response.body.comments[1].id == COMMENT_ID_1.toString()
        response.body.comments[2].id == COMMENT_ID_2.toString()
    }

    def "should return comments only for the specified article id"() {
        given:
        thereIsASavedComment(aComment().withId(COMMENT_ID_1).withArticleId(ARTICLE_ID_1).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_2).withArticleId(ARTICLE_ID_2).build())

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1)

        then:
        response.body.count == 1
        response.body.comments.size() == 1
        response.body.comments[0].id == COMMENT_ID_1.toString()
    }

    def "should return paged comments"() {
        given:
        thereIsASavedComment(aComment().withId(COMMENT_ID_1).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_1).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_2).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_2).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_3).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_3).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_4).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_4).build())
        thereIsASavedComment(aComment().withId(COMMENT_ID_5).withArticleId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_5).build())

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 2, 3)

        then:
        response.statusCode == HttpStatus.OK
        response.body.comments.size() == 2
        response.body.count == 2
        response.body.comments[0].id == COMMENT_ID_2.toString()
        response.body.comments[1].id == COMMENT_ID_1.toString()
    }

    def "should return empty list when page is empty"() {
        given:
        thereIsASavedComment(aComment().withArticleId(ARTICLE_ID_1).build())

        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 2, 10)

        then:
        response.statusCode == HttpStatus.OK
        response.body.count == 0
        response.body.comments.size() == 0
    }

    @Unroll
    def "should return 400 when page param is #desc"() {
        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, page)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        where:
        page | desc
        -1   | "negative"
        0    | "zero"
    }

    def "should return 400 when pageSize param is #desc"() {
        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 1, pageSize)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        where:
        pageSize | desc
        -1       | "negative"
        0        | "zero"
        51       | "more than 50"
    }

    def "should not return 400 when pageSize param is 50"() {
        when:
        def response = getCommentsRequestIsSent(ARTICLE_ID_1, 1, 50)

        then:
        response.statusCode == HttpStatus.OK
    }

    ResponseEntity<Map> getCommentsRequestIsSent(UUID articleId, int page = 1, int pageSize = 10) {
        restTemplate.getForEntity(url("/articles/$articleId/comments?page=$page&pageSize=$pageSize"), Map)
    }

    void thereIsASavedComment(Comment comment) {
        mongoTemplate.insert(comment)
    }
}

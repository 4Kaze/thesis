package pl.lodz.p.stanczyk.articleservice.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.articleservice.IntegrationSpec
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article

import java.time.Instant

import static pl.lodz.p.stanczyk.articleservice.api.ArticleBuilder.anArticle

class GetArticlesEndpointSpec extends IntegrationSpec {

    static final def ARTICLE_ID_1 = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    static final def ARTICLE_ID_2 = UUID.fromString("6d617ac6-c694-4f1a-8916-7742b0f98f92")
    static final def ARTICLE_ID_3 = UUID.fromString("0e41039b-1e8e-4853-9244-12cef55694c1")
    static final def ARTICLE_ID_4 = UUID.fromString("bfde8f3c-00eb-4489-975c-0054941de2be")
    static final def ARTICLE_ID_5 = UUID.fromString("b98cb5b2-542a-4db0-9419-1a36685810e7")
    static final def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def PUBLICATION_DATE_1 = Instant.parse("2007-12-03T10:00:00.00Z")
    def PUBLICATION_DATE_2 = Instant.parse("2007-12-03T10:01:00.00Z")
    def PUBLICATION_DATE_3 = Instant.parse("2007-12-03T10:10:00.00Z")
    def PUBLICATION_DATE_4 = Instant.parse("2007-12-03T10:11:00.00Z")
    def PUBLICATION_DATE_5 = Instant.parse("2007-12-03T10:12:00.00Z")

    def "should return stored articles"() {
        given:
        def article = anArticle()
                .withId(ARTICLE_ID_1)
                .withTitle("Title")
                .withContent("Content")
                .withAuthorId(USER_ID)
                .withPublicationDate(PUBLICATION_DATE_1)
                .build()

        and:
        thereIsAStoredArticle(article)

        when:
        def response = getLatestArticleRequestIsSent()

        then:
        response.statusCode == HttpStatus.OK
        response.body.count == 1
        response.body.articles.size() == 1
        with(response.body.articles[0]) {
            id == ARTICLE_ID_1.toString()
            title == "Title"
            content == "Content"
            authorId == USER_ID.toString()
            Instant.parse(publicationDate) == PUBLICATION_DATE_1
        }
    }

    def "should return stored articles sorted by publication date"() {
        given:
        thereIsAStoredArticle(
                anArticle()
                        .withId(ARTICLE_ID_1)
                        .withPublicationDate(PUBLICATION_DATE_3)
                        .build()
        )

        thereIsAStoredArticle(
                anArticle()
                        .withId(ARTICLE_ID_2)
                        .withPublicationDate(PUBLICATION_DATE_1)
                        .build()
        )
        thereIsAStoredArticle(
                anArticle()
                        .withId(ARTICLE_ID_3)
                        .withPublicationDate(PUBLICATION_DATE_2)
                        .build()
        )

        when:
        def response = getLatestArticleRequestIsSent()

        then:
        response.statusCode == HttpStatus.OK
        response.body.count == 3
        response.body.articles.size() == 3
        response.body.articles[0].id == ARTICLE_ID_1.toString()
        response.body.articles[1].id == ARTICLE_ID_3.toString()
        response.body.articles[2].id == ARTICLE_ID_2.toString()
    }

    def "should return paged articles"() {
        given:
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID_1).withPublicationDate(PUBLICATION_DATE_1).build())
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID_2).withPublicationDate(PUBLICATION_DATE_2).build())
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID_3).withPublicationDate(PUBLICATION_DATE_3).build())
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID_4).withPublicationDate(PUBLICATION_DATE_4).build())
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID_5).withPublicationDate(PUBLICATION_DATE_5).build())

        when:
        def response = getLatestArticleRequestIsSent(2, 3)

        then:
        response.statusCode == HttpStatus.OK
        response.body.articles.size() == 2
        response.body.count == 2
        response.body.articles[0].id == ARTICLE_ID_2.toString()
        response.body.articles[1].id == ARTICLE_ID_1.toString()
    }

    def "should return empty list when page is empty"() {
        given:
        thereIsAStoredArticle(anArticle().build())

        when:
        def response = getLatestArticleRequestIsSent(2, 10)

        then:
        response.statusCode == HttpStatus.OK
        response.body.count == 0
        response.body.articles.size() == 0
    }

    def "should return 400 when page param is #desc"() {
        when:
        def response = getLatestArticleRequestIsSent(page)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        where:
        page | desc
        -1   | "negative"
        0    | "zero"
    }

    def "should return 400 when pageSize param is #desc"() {
        when:
        def response = getLatestArticleRequestIsSent(1, pageSize)

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
        def response = getLatestArticleRequestIsSent(1, 50)

        then:
        response.statusCode == HttpStatus.OK
    }

    ResponseEntity<Map> getLatestArticleRequestIsSent(int page = 1, int pageSize = 10) {
        restTemplate.getForEntity(url("/articles?pageSize=$pageSize&page=$page"), Map)
    }

    Article thereIsAStoredArticle(Article article) {
        mongoTemplate.save(article)
    }

    void thereAreNoArticlesStored() {
        mongoTemplate.getDb().drop()
    }
}

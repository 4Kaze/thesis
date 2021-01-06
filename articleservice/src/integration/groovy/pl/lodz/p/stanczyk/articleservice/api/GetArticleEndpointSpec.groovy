package pl.lodz.p.stanczyk.articleservice.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.articleservice.IntegrationSpec
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article

import java.time.Instant

import static pl.lodz.p.stanczyk.articleservice.api.ArticleBuilder.anArticle

class GetArticleEndpointSpec extends IntegrationSpec {

    static final def ARTICLE_ID = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    static final def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def PUBLICATION_DATE = Instant.parse("2007-12-03T10:15:30.00Z")

    def "should return stored article"() {
        given:
        def article = anArticle()
                .withId(ARTICLE_ID)
                .withTitle("Title")
                .withContent("Content")
                .withAuthorId(USER_ID)
                .withPublicationDate(PUBLICATION_DATE)
                .build()

        and:
        thereIsAStoredArticle(article)

        when:
        def response = getArticleRequestIsSent(ARTICLE_ID)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body) {
            id == ARTICLE_ID.toString()
            title == "Title"
            content == "Content"
            authorId == USER_ID.toString()
            Instant.parse(publicationDate) == PUBLICATION_DATE
        }
    }

    def "should return 404 when article does not exist"() {
        given:
        thereAreNoArticlesStored()

        when:
        def response = getArticleRequestIsSent(ARTICLE_ID)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    ResponseEntity<Map> getArticleRequestIsSent(UUID articleId) {
        restTemplate.getForEntity(url("/articles/$articleId"), Map)
    }

    Article thereIsAStoredArticle(Article article) {
        mongoTemplate.save(article)
    }

    void thereAreNoArticlesStored() {
        mongoTemplate.getDb().drop()
    }
}

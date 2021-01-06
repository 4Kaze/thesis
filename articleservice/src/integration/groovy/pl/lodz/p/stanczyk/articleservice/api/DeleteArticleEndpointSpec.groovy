package pl.lodz.p.stanczyk.articleservice.api

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.articleservice.IntegrationSpec
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article
import pl.lodz.p.stanczyk.articleservice.oauth.BearerTokenCreator

import static pl.lodz.p.stanczyk.articleservice.api.ArticleBuilder.anArticle

class DeleteArticleEndpointSpec extends IntegrationSpec implements BearerTokenCreator {

    static final def ARTICLE_ID = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    static final def USER_ID_1 = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def USER_ID_2 = UUID.fromString("c4abd28f-61b2-4d5a-9487-60ec63ceaa18")
    def USER_1_WRITER_TOKEN

    def setup() {
        USER_1_WRITER_TOKEN = oAuthTokenFor(USER_ID_1.toString(), "username", "User's name", "writer", "reader")
    }

    def "should delete stored article"() {
        given:
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID).withAuthorId(USER_ID_1).build())

        when:
        def response = deleteArticleRequestIsSent(ARTICLE_ID, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.NO_CONTENT
        assertNoArticlesAreStored()
    }

    def "should delete stored article when user has admin role"() {
        given:
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID).withAuthorId(USER_ID_1).build())
        def adminToken = oAuthTokenFor(USER_ID_2.toString(), "username", "User's name", "admin")

        when:
        def response = deleteArticleRequestIsSent(ARTICLE_ID, adminToken)

        then:
        response.statusCode == HttpStatus.NO_CONTENT
        assertNoArticlesAreStored()
    }

    def "should return 404 when article does not exist"() {
        given:
        thereAreNoArticlesStored()

        when:
        def response = deleteArticleRequestIsSent(ARTICLE_ID, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return 403 when user is not an author of the article"() {
        given:
        thereIsAStoredArticle(
                anArticle()
                        .withId(ARTICLE_ID)
                        .withAuthorId(USER_ID_2)
                        .build()
        )

        when:
        def response = deleteArticleRequestIsSent(ARTICLE_ID, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
        assertArticleWasNotRemoved(ARTICLE_ID)
    }

    def "should return 401 when no auth token is provided"() {
        given:
        thereIsAStoredArticle(
                anArticle()
                        .withId(ARTICLE_ID)
                        .withAuthorId(USER_ID_2)
                        .build()
        )

        when:
        def response = deleteArticleRequestIsSent(ARTICLE_ID, null)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
        assertArticleWasNotRemoved(ARTICLE_ID)
    }

    ResponseEntity<Map> deleteArticleRequestIsSent(UUID articleId, String token) {
        def headers = new HttpHeaders()
        if (token != null) {
            headers.add("Authorization", "Bearer $token")
        }
        def entity = new HttpEntity(null, headers)
        restTemplate.exchange(url("/articles/$articleId"), HttpMethod.DELETE, entity, Map) as ResponseEntity<Map>
    }

    Article thereIsAStoredArticle(Article article) {
        mongoTemplate.save(article)
    }

    void thereAreNoArticlesStored() {
        mongoTemplate.getDb().drop()
    }

    void assertNoArticlesAreStored() {
        assert mongoTemplate.getCollection(mongoTemplate.getCollectionName(Article)).countDocuments() == 0
    }

    void assertArticleWasNotRemoved(UUID articleId) {
        assert mongoTemplate.findById(articleId.toString(), Article) != null
    }
}

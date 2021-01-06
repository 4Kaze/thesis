package pl.lodz.p.stanczyk.articleservice.api

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.articleservice.IntegrationSpec
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article
import pl.lodz.p.stanczyk.articleservice.oauth.BearerTokenCreator
import spock.lang.Unroll

import static ArticleRequestBuilder.anArticleRequest
import static pl.lodz.p.stanczyk.articleservice.api.ArticleBuilder.anArticle

class ModifyArticleEndpointSpec extends IntegrationSpec implements BearerTokenCreator {

    static final def ARTICLE_ID = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    static final def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def USER_ID_2 = UUID.fromString("c4abd28f-61b2-4d5a-9487-60ec63ceaa18")
    def USER_1_WRITER_TOKEN

    def setup() {
        USER_1_WRITER_TOKEN = oAuthTokenFor(USER_ID.toString(), "username", "User's name", "writer", "reader")
    }

    def "should return modified article"() {
        given:
        thereIsAStoredArticle(
                anArticle()
                        .withId(ARTICLE_ID)
                        .withTitle("Title")
                        .withContent("Content")
                        .build()
        )

        when:
        def response = modifyArticleRequestIsSent(
                ARTICLE_ID,
                anArticleRequest()
                        .withTitle("Different title")
                        .withContent("Different content")
                        .build(),
                USER_1_WRITER_TOKEN
        )

        then:
        response.statusCode == HttpStatus.OK
        response.body.title == "Different title"
        response.body.content == "Different content"
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
        def response = modifyArticleRequestIsSent(ARTICLE_ID, anArticleRequest().build(), USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }

    def "should return 401 when no auth token is provided"() {
        when:
        def response = modifyArticleRequestIsSent(ARTICLE_ID, anArticleRequest().build(), null)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    @Unroll
    def "should modify article when title #desc"() {
        given:
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID).build())
        def request = anArticleRequest()
                .withTitle(title)
                .build()

        when:
        def response = modifyArticleRequestIsSent(ARTICLE_ID, request, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
        with(storedArticle(response.body.id)) {
            title == request.title
        }

        where:
        title    || desc
        "1"      || "is one character long"
        "1" * 50 || "is 50 characters long"
    }

    @Unroll
    def "should not modify article when title #desc"() {
        given:
        thereIsAStoredArticle(anArticle().withTitle("Original title").withId(ARTICLE_ID).build())
        def request = anArticleRequest()
                .withTitle(title)
                .build()

        when:
        def response = modifyArticleRequestIsSent(ARTICLE_ID, request, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        storedArticle(ARTICLE_ID.toString()).title == "Original title"

        where:
        title    || desc
        " " * 10 || "is blank"
        ""       || "is empty"
        null     || "is null"
        "1" * 51 || "is longer than 50 characters"
    }

    @Unroll
    def "should modify article when content #desc"() {
        given:
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID).build())
        def request = anArticleRequest()
                .withContent(content)
                .build()

        when:
        def response = modifyArticleRequestIsSent(ARTICLE_ID, request, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
        with(storedArticle(response.body.id)) {
            content == request.content
        }

        where:
        content || desc
        "1"     || "is one character long"
    }

    @Unroll
    def "should not modify article when content #desc"() {
        given:
        thereIsAStoredArticle(anArticle().withContent("Original content").withId(ARTICLE_ID).build())
        def request = anArticleRequest()
                .withContent(content)
                .build()

        when:
        def response = modifyArticleRequestIsSent(ARTICLE_ID, request, USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        storedArticle(ARTICLE_ID.toString()).content == "Original content"

        where:
        content  || desc
        " " * 10 || "is blank"
        ""       || "is empty"
        null     || "is null"
    }

    def "should return 404 when article does not exist"() {
        given:
        thereAreNoArticlesStored()

        when:
        def response = modifyArticleRequestIsSent(ARTICLE_ID, anArticleRequest().build(), USER_1_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should update updatedAt field of the article"() {
        given:
        thereIsAStoredArticle(anArticle().withId(ARTICLE_ID).withUpdatedAt(null).build())

        and:
        def currentTime = nowInMillis()
        theTimeIs(currentTime)

        when:
        modifyArticleRequestIsSent(ARTICLE_ID, anArticleRequest().build(), USER_1_WRITER_TOKEN)

        then:
        storedArticle(ARTICLE_ID.toString()).updatedAt == currentTime
    }

    ResponseEntity<Map> modifyArticleRequestIsSent(UUID articleId, Map request, String token) {
        def headers = new HttpHeaders()
        if (token != null) {
            headers.add("Authorization", "Bearer $token")
        }
        def entity = new HttpEntity(request, headers)
        restTemplate.exchange(url("/articles/$articleId"), HttpMethod.PUT, entity, Map) as ResponseEntity<Map>
    }

    Article storedArticle(String articleId) {
        mongoTemplate.findById(articleId, Article)
    }

    Article thereIsAStoredArticle(Article article) {
        mongoTemplate.save(article)
    }

    void thereAreNoArticlesStored() {
        mongoTemplate.getDb().drop()
    }
}

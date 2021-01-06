package pl.lodz.p.stanczyk.articleservice.api


import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.articleservice.IntegrationSpec
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article
import pl.lodz.p.stanczyk.articleservice.oauth.BearerTokenCreator
import spock.lang.Unroll

import java.time.Instant

import static ArticleRequestBuilder.anArticleRequest

class CreateArticleEndpointSpec extends IntegrationSpec implements BearerTokenCreator {

    static final def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    static final def USERNAME = "username"
    static final def NAME = "User's name"
    def USER_WRITER_TOKEN

    def setup() {
        USER_WRITER_TOKEN = oAuthTokenFor(USER_ID.toString(), USERNAME, NAME, "writer", "reader")
    }

    def "should return created article"() {
        given:
        def request = anArticleRequest()
                .withTitle("Article title")
                .withContent("Article content")
                .build()

        and:
        def currentTime = nowInMillis()
        theTimeIs(currentTime)

        when:
        def response = createArticleRequestIsSent(request, USER_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body) {
            id != null
            title == "Article title"
            content == "Article content"
            Instant.parse(publicationDate) == currentTime
            authorId == CreateArticleEndpointSpec.USER_ID.toString()
        }
    }

    def "should return 403 when user does not have a writer role"() {
        given:
        def request = anArticleRequest().build()

        and:
        def readerToken = oAuthTokenFor(USER_ID.toString(), USERNAME, NAME, "reader")

        when:
        def response = createArticleRequestIsSent(request, readerToken)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }

    def "should return 401 when no auth token is provided"() {
        given:
        def request = anArticleRequest().build()

        when:
        def response = createArticleRequestIsSent(request, null)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    @Unroll
    def "should create article when title #desc"() {
        given:
        def request = anArticleRequest()
                .withTitle(title)
                .build()

        when:
        def response = createArticleRequestIsSent(request, USER_WRITER_TOKEN)

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
    def "should not create article when title #desc"() {
        given:
        def request = anArticleRequest()
                .withTitle(title)
                .build()

        when:
        def response = createArticleRequestIsSent(request, USER_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        noArticleWasStored()

        where:
        title    || desc
        " " * 10 || "is blank"
        ""       || "is empty"
        null     || "is null"
        "1" * 51 || "is longer than 50 characters"
    }

    @Unroll
    def "should create article when content #desc"() {
        given:
        def request = anArticleRequest()
                .withContent(content)
                .build()

        when:
        def response = createArticleRequestIsSent(request, USER_WRITER_TOKEN)

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
    def "should not create article when content #desc"() {
        given:
        def request = anArticleRequest()
                .withContent(content)
                .build()

        when:
        def response = createArticleRequestIsSent(request, USER_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST

        and:
        noArticleWasStored()

        where:
        content  || desc
        " " * 10 || "is blank"
        ""       || "is empty"
        ""       || "is empty"
        null     || "is null"
    }

    ResponseEntity<Map> createArticleRequestIsSent(Map request, String token) {
        def headers = new HttpHeaders()
        if(token != null) {
            headers.add("Authorization", "Bearer $token")
        }
        def entity = new HttpEntity(request, headers)
        restTemplate.postForEntity(url("/articles"), entity, Map)
    }

    Article storedArticle(String articleId) {
        mongoTemplate.findById(articleId, Article)
    }

    void noArticleWasStored() {
        assert mongoTemplate.getCollection(mongoTemplate.getCollectionName(Article)).countDocuments() == 0
    }
}

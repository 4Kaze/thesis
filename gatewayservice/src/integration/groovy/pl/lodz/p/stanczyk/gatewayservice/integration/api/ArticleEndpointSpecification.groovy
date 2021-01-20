package pl.lodz.p.stanczyk.gatewayservice.integration.api

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec
import pl.lodz.p.stanczyk.gatewayservice.integration.oauth.BearerTokenCreator
import pl.lodz.p.stanczyk.gatewayservice.integration.stub.ArticleServiceStub
import pl.lodz.p.stanczyk.gatewayservice.integration.stub.UserServiceStub

import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.ArticleBuilder.anArticle
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.ArticleListBuilder.anArticleList
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.DraftArticleRequestBuilder.aDraftArticleRequest
import static pl.lodz.p.stanczyk.gatewayservice.integration.builder.UserBuilder.aUser

class ArticleEndpointSpecification extends IntegrationSpec implements ArticleServiceStub, UserServiceStub, BearerTokenCreator {
    static final def ARTICLE_ID_1 = "569d67ec-5565-4a71-a7ec-abfbcd82dfac"
    static final def ARTICLE_ID_2 = "f585a9c0-6ee0-442b-bde7-14aeaf9267c9"
    static final def AUTHOR_ID_1 = "32b6abf2-ff57-451b-8561-a15a02258c2f"
    static final def AUTHOR_ID_2 = "73dee293-446f-4629-88a3-02e377dcd581"
    def USER_WRITER_TOKEN

    def setup() {
        USER_WRITER_TOKEN = oAuthTokenFor(AUTHOR_ID_1.toString(), "username", "name", "writer", "reader")
    }

    def "should fetch the article and enrich it with author's name"() {
        given:
        def article = anArticle().withId(ARTICLE_ID_1).withAuthorId(AUTHOR_ID_1).build()
        stubGetArticle(ARTICLE_ID_1, article)
        stubGetUsers([AUTHOR_ID_1], [aUser(AUTHOR_ID_1, "Author Name")])

        when:
        def response = getArticleRequestIsSent(ARTICLE_ID_1)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body) {
            title == article.title
            content == article.content
            publicationDate == article.publicationDate
            with(author) {
                id == ArticleEndpointSpecification.AUTHOR_ID_1
                name == "Author Name"
            }
        }
    }

    def "should fetch a list of articles and enrich them with authors' names"() {
        given:
        def article1 = anArticle().withId(ARTICLE_ID_1).withAuthorId(AUTHOR_ID_1)
        def article2 = anArticle().withId(ARTICLE_ID_2).withAuthorId(AUTHOR_ID_2)
        stubGetArticles(1, 10, anArticleList().withArticles([article1, article2]).build())
        stubGetUsers([AUTHOR_ID_1, AUTHOR_ID_2], [aUser(AUTHOR_ID_1, "Author 1"), aUser(AUTHOR_ID_2, "Author 2")])

        when:
        def response = getArticlesRequestIsSent(1, 10)

        then:
        response.statusCode == HttpStatus.OK
        with(response.body.articles[0]) {
            id == ARTICLE_ID_1
            author.id == AUTHOR_ID_1
            author.name == "Author 1"
        }
        with(response.body.articles[1]) {
            id == ARTICLE_ID_2
            author.id == AUTHOR_ID_2
            author.name == "Author 2"
        }
    }

    def "should proxy a create article request"() {
        given:
        def article = anArticle().withId(ARTICLE_ID_1).build()
        def createArticleRequest = aDraftArticleRequest().build()
        stubCreateArticle(createArticleRequest, article, USER_WRITER_TOKEN)

        when:
        def response = createArticleRequestIsSent(createArticleRequest, USER_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.OK
        response.body.id == ARTICLE_ID_1
    }

//    def "should proxy a modify article request"() {
//        given:
//        def article = anArticle().withId(ARTICLE_ID_1).build()
//        def modifyArticleRequest = aDraftArticleRequest().build()
//        stubModifyArticle(ARTICLE_ID_1, modifyArticleRequest, article, USER_WRITER_TOKEN)
//
//        when:
//        def response = modifyArticleRequestIsSent(ARTICLE_ID_1, modifyArticleRequest, USER_WRITER_TOKEN)
//
//        then:
//        response.statusCode == HttpStatus.OK
//        response.body.id == ARTICLE_ID_1
//    }

    def "should proxy a delete article request"() {
        given:
        stubDeleteArticle(ARTICLE_ID_1, USER_WRITER_TOKEN)

        when:
        def response = deleteArticleRequestIsSent(ARTICLE_ID_1, USER_WRITER_TOKEN)

        then:
        response.statusCode == HttpStatus.NO_CONTENT
    }

    def "should pass an error response from article service"() {
        given:
        def article = anArticle().withId(ARTICLE_ID_1).withAuthorId(AUTHOR_ID_1).build()
        stubGetArticle(ARTICLE_ID_1, ["message": "some error message"], 404)

        when:
        def response = getArticleRequestIsSent(ARTICLE_ID_1)

        then:
        response.statusCodeValue == 404
        response.body.message == "some error message"
    }

    def "should not fail on error from user service"() {
        given:
        def article = anArticle().withId(ARTICLE_ID_1).withAuthorId(AUTHOR_ID_1).build()
        stubGetArticle(ARTICLE_ID_1, article)
        stubGetUsers([AUTHOR_ID_1], ["message": "some error message"], 500)

        when:
        def response = getArticleRequestIsSent(ARTICLE_ID_1)

        then:
        response.statusCodeValue == 200
        with(response.body) {
            id == ARTICLE_ID_1
            author.id == AUTHOR_ID_1
            author.name == null
        }
    }

    ResponseEntity<Map> getArticleRequestIsSent(String articleId) {
        restTemplate.getForEntity(url("/articles/$articleId"), Map)
    }

    ResponseEntity<Map> getArticlesRequestIsSent(int page, int pageSize) {
        restTemplate.getForEntity(url("/articles?page=$page&pageSize=$pageSize"), Map)
    }

    ResponseEntity<Map> createArticleRequestIsSent(Map request, String token) {
        def headers = new HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        def entity = new HttpEntity(request, headers)
        restTemplate.postForEntity(url("/articles"), entity, Map)
    }

    ResponseEntity<Map> modifyArticleRequestIsSent(String articleId, Map request, String token) {
        def headers = new HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        def entity = new HttpEntity(request, headers)
        restTemplate.exchange(url("/articles/$articleId"), HttpMethod.PUT, entity, Map) as ResponseEntity<Map>
    }

    ResponseEntity<Map> deleteArticleRequestIsSent(String articleId, String token) {
        def headers = new HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        def entity = new HttpEntity(null, headers)
        restTemplate.exchange(url("/articles/$articleId"), HttpMethod.DELETE, entity, Map) as ResponseEntity<Map>
    }
}

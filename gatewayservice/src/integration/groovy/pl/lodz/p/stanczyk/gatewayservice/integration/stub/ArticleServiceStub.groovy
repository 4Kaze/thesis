package pl.lodz.p.stanczyk.gatewayservice.integration.stub

import groovy.json.JsonOutput
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.delete
import static pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec.wireMockServer

trait ArticleServiceStub {
    void stubGetArticle(String articleId, Map response, int status = 200) {
        wireMockServer.stubFor(get("/article-service/articles/$articleId")
                .willReturn(
                        aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(JsonOutput.toJson(response))
                                .withStatus(status)
                )
        )
    }

    void stubGetArticles(int page, int pageSize, Map response, int status = 200) {
        wireMockServer.stubFor(get("/article-service/articles?page=$page&pageSize=$pageSize")
                .willReturn(
                        aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(JsonOutput.toJson(response))
                                .withStatus(status)
                )
        )
    }

    void stubCreateArticle(Map request, Map response, String authToken, int status = 200) {
        wireMockServer.stubFor(
                post("/article-service/articles")
                        .withRequestBody(equalToJson(JsonOutput.toJson(request)))
                        .withHeader("Authorization", equalTo("Bearer $authToken"))
                        .willReturn(
                                aResponse()
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(JsonOutput.toJson(response))
                                        .withStatus(status)
                        )
        )
    }

    void stubModifyArticle(String articleId, Map request, Map response, String authToken, int status = 200) {
        wireMockServer.stubFor(
                put("/article-service/articles/$articleId")
                        .withRequestBody(equalToJson(JsonOutput.toJson(request)))
                        .withHeader("Authorization", equalTo("Bearer $authToken"))
                        .willReturn(
                                aResponse()
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(JsonOutput.toJson(response))
                                        .withStatus(status)
                        )
        )
    }

    void stubDeleteArticle(String articleId, String authToken, int status = 204) {
        wireMockServer.stubFor(
                delete("/article-service/articles/$articleId")
                        .withHeader("Authorization", equalTo("Bearer $authToken"))
                        .willReturn(
                                aResponse()
                                        .withStatus(status)
                        )
        )
    }
}
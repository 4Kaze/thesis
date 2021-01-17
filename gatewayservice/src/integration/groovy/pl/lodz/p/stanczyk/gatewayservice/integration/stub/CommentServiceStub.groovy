package pl.lodz.p.stanczyk.gatewayservice.integration.stub

import groovy.json.JsonOutput
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static pl.lodz.p.stanczyk.gatewayservice.integration.IntegrationSpec.wireMockServer

trait CommentServiceStub {
    void stubGetComments(String articleId, int page, int pageSize, Map response, int status = 200) {
        wireMockServer.stubFor(get("/comment-service/articles/$articleId/comments?page=$page&pageSize=$pageSize")
                .willReturn(
                        aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(JsonOutput.toJson(response))
                                .withStatus(status)
                )
        )
    }

    void stubAddComment(String articleId, Map request, Map response, String authToken, int status = 200) {
        wireMockServer.stubFor(
                post("/comment-service/articles/$articleId/comments")
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

    void stubDeleteComment(String articleId, String commentId, String authToken, int status = 204) {
        wireMockServer.stubFor(
                delete("/comment-service/articles/$articleId/comments/$commentId")
                        .withHeader("Authorization", equalTo("Bearer $authToken"))
                        .willReturn(
                                aResponse()
                                        .withStatus(status)
                        )
        )
    }
}
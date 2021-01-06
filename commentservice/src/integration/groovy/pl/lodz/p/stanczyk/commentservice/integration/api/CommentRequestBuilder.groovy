package pl.lodz.p.stanczyk.commentservice.integration.api

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CommentRequestBuilder {
    String content = "Some content"

    static CommentRequestBuilder aCommentRequest() {
        new CommentRequestBuilder()
    }

    Map build() {
        [
                "content": content
        ]
    }
}

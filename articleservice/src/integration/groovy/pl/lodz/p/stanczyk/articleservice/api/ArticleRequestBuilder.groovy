package pl.lodz.p.stanczyk.articleservice.api

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ArticleRequestBuilder {
    String title = "Article title"
    String content = "Article content"

    static ArticleRequestBuilder anArticleRequest() {
        new ArticleRequestBuilder()
    }

    Map build() {
        [
                "title": title,
                "content": content
        ]
    }
}

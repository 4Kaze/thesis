package pl.lodz.p.stanczyk.gatewayservice.integration.builder

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import java.time.Instant

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ArticleBuilder {
    String id = "1bf95bb7-2845-4b85-b214-f3ea2b1d02e8"
    String title = "Article title"
    String content = "Article content"
    Instant publicationDate = Instant.parse("2007-12-03T10:15:30.00Z")
    String authorId = "3060611d-6427-4611-bcce-f2f57c0808ac"

    static ArticleBuilder anArticle() {
        new ArticleBuilder()
    }

    Map build() {
        [
                id: id,
                title: title,
                content: content,
                publicationDate: publicationDate.toString(),
                authorId: authorId
        ]
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class DraftArticleRequestBuilder {
    String title = "Article title"
    String content = "Article content"

    static DraftArticleRequestBuilder aDraftArticleRequest() {
        new DraftArticleRequestBuilder()
    }

    Map build() {
        [
                title: title,
                content: content
        ]
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ArticleListBuilder {
    List<ArticleBuilder> articles

    static ArticleListBuilder anArticleList() {
        new ArticleListBuilder()
    }

    Map build() {
        [
                articles: articles.collect {it.build() },
                count: articles.size()
        ]
    }
}
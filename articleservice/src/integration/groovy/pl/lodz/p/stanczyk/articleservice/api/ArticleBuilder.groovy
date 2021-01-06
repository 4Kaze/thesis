package pl.lodz.p.stanczyk.articleservice.api

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article

import java.time.Instant

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ArticleBuilder {
    UUID id = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    String title = "Article title"
    String content = "Article content"
    Instant publicationDate = Instant.parse("2007-12-03T10:15:30.00Z")
    Instant updatedAt
    UUID authorId = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")

    static ArticleBuilder anArticle() {
        new ArticleBuilder()
    }

    Article build() {
        new Article(
                id.toString(),
                title,
                content,
                publicationDate,
                updatedAt,
                authorId
        )
    }
}

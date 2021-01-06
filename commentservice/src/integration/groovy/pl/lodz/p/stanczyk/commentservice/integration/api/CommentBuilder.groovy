package pl.lodz.p.stanczyk.commentservice.integration.api

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.Comment

import java.time.Instant

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CommentBuilder {
    UUID id = UUID.fromString("569d67ec-5565-4a71-a7ec-abfbcd82dfac")
    String content = "Comment content"
    Instant publicationDate = Instant.parse("2007-12-03T10:15:30.00Z")
    UUID authorId = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")
    UUID articleId = UUID.fromString("996a2d25-c74c-43f3-afbe-47e3cdb264c2")

    static CommentBuilder aComment() {
        new CommentBuilder()
    }

    Comment build() {
        new Comment(
                id.toString(),
                content,
                publicationDate,
                authorId,
                articleId
        )
    }
}

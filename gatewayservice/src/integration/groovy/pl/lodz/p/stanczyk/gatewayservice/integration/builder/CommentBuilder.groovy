package pl.lodz.p.stanczyk.gatewayservice.integration.builder

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CommentBuilder {
    String id = "569d67ec-5565-4a71-a7ec-abfbcd82dfac"
    String content = "Comment content"
    String publicationTime = "2007-12-03T10:15:30.00Z"
    String authorId = "32b6abf2-ff57-451b-8561-a15a02258c2f"

    static CommentBuilder aComment() {
        new CommentBuilder()
    }

    Map build() {
        [
                id             : id,
                content        : content,
                publicationTime: publicationTime,
                authorId       : authorId
        ]
    }
}


@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CommentListBuilder {
    List<CommentBuilder> comments

    static CommentListBuilder aCommentsList() {
        new CommentListBuilder()
    }

    Map build() {
        [
                comments: comments.collect { it.build() },
                count   : comments.size()
        ]
    }
}

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CommentDraftBuilder {
    String content = "Comment content"

    static CommentDraftBuilder aCommentDraft() {
        new CommentDraftBuilder()
    }

    Map build() {
        [
                content: content
        ]
    }
}
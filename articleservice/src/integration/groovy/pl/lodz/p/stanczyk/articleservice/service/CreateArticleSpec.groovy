package pl.lodz.p.stanczyk.articleservice.service

import org.springframework.beans.factory.annotation.Autowired
import pl.lodz.p.stanczyk.articleservice.IntegrationSpec
import pl.lodz.p.stanczyk.articleservice.domain.ArticleId
import pl.lodz.p.stanczyk.articleservice.domain.ArticleService
import pl.lodz.p.stanczyk.articleservice.domain.ArticleDraft
import pl.lodz.p.stanczyk.articleservice.domain.AuthorId
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.Article
import spock.lang.Subject

class CreateArticleSpec extends IntegrationSpec {

    @Autowired
    @Subject
    ArticleService articleService

    def USER_ID = UUID.fromString("32b6abf2-ff57-451b-8561-a15a02258c2f")

    def "should store article"() {
        given:
        def article = new ArticleDraft("Article title", "Article content", new AuthorId(USER_ID))

        and:
        def currentTime = nowInMillis()
        theTimeIs(currentTime)

        when:
        def createdArticle = articleService.createArticle(article)

        then:
        with(storedArticle(createdArticle.id)) {
            title == "Article title"
            content == "Article content"
            publicationDate == currentTime
            authorId == USER_ID
            updatedAt == null
        }
    }

    Article storedArticle(ArticleId articleId) {
        mongoTemplate.findById(articleId.value.toString(), Article)
    }
}

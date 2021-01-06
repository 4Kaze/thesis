package pl.lodz.p.stanczyk.articleservice


import pl.lodz.p.stanczyk.articleservice.domain.ArticleService
import pl.lodz.p.stanczyk.articleservice.infrastructure.api.ArticleEndpoint
import spock.lang.Specification
import spock.lang.Subject

class EndpointSpec extends Specification {
    @Subject
    ArticleEndpoint articleEndpoint
    private ArticleService articleService
    private ArticleRepository

    void setup() {
        articleService = new ArticleService()
        articleEndpoint = new ArticleEndpoint(articleService)
    }
}

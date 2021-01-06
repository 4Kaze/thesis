package pl.lodz.p.stanczyk.articleservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import pl.lodz.p.stanczyk.articleservice.adapter.MongoArticleRepositoryAdapter
import pl.lodz.p.stanczyk.articleservice.domain.port.ArticleRepository
import pl.lodz.p.stanczyk.articleservice.domain.ArticleService
import pl.lodz.p.stanczyk.articleservice.infrastructure.InstantNowSupplier
import pl.lodz.p.stanczyk.articleservice.infrastructure.InstantNowSupplierImpl
import pl.lodz.p.stanczyk.articleservice.infrastructure.mongo.MongoArticleRepository

@Configuration
class ApplicationConfig {
    @Bean
    fun articleService(mongoUserRepository: MongoArticleRepository) = ArticleService(
        articleRepository = articleRepository(mongoUserRepository),
        instantNowSupplier = instantNowSupplier()
    )

    @Bean
    fun articleRepository(mongoUserRepository: MongoArticleRepository): ArticleRepository =
        MongoArticleRepositoryAdapter(
            mongoRepository = mongoUserRepository
        )

    @Bean
    fun instantNowSupplier(): InstantNowSupplier = InstantNowSupplierImpl()
}

package pl.lodz.p.stanczyk.gatewayservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.lodz.p.stanczyk.gatewayservice.adapter.ArticleServiceAdapter
import pl.lodz.p.stanczyk.gatewayservice.adapter.CommentServiceAdapter
import pl.lodz.p.stanczyk.gatewayservice.adapter.UserServiceAdapter
import pl.lodz.p.stanczyk.gatewayservice.domain.article.ArticleService
import pl.lodz.p.stanczyk.gatewayservice.domain.comment.CommentService
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.ArticleServiceClient
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.CommentServiceClient
import pl.lodz.p.stanczyk.gatewayservice.infrastructure.client.UserServiceClient

@Configuration
class ApplicationConfig {
    @Bean
    fun articleService(articleServiceClient: ArticleServiceClient, userServiceClient: UserServiceClient) =
        ArticleService(
            articleProvider(articleServiceClient),
            userProvider(userServiceClient)
        )

    @Bean
    fun commentService(articleServiceClient: ArticleServiceClient, commentServiceClient: CommentServiceClient, userServiceClient: UserServiceClient) =
        CommentService(
            articleProvider(articleServiceClient),
            commentProvider(commentServiceClient),
            userProvider(userServiceClient)
        )

    @Bean
    fun articleProvider(articleServiceClient: ArticleServiceClient) = ArticleServiceAdapter(articleServiceClient)

    @Bean
    fun commentProvider(commentServiceClient: CommentServiceClient) = CommentServiceAdapter(commentServiceClient)

    @Bean
    fun userProvider(userServiceClient: UserServiceClient) = UserServiceAdapter(userServiceClient)
}
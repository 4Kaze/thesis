package pl.lodz.p.stanczyk.commentservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.lodz.p.stanczyk.commentservice.adapter.MongoCommentRepositoryAdapter
import pl.lodz.p.stanczyk.commentservice.domain.CommentService
import pl.lodz.p.stanczyk.commentservice.infrastructure.InstantNowSupplier
import pl.lodz.p.stanczyk.commentservice.infrastructure.InstantNowSupplierImpl
import pl.lodz.p.stanczyk.commentservice.infrastructure.mongo.MongoCommentRepository

@Configuration
class ApplicationConfig {
    @Bean
    fun commentService(mongoCommentRepository: MongoCommentRepository): CommentService = CommentService(
        commentRepository = commentRepository(mongoCommentRepository),
        instantNowSupplier = instantNowSupplier()
    )

    @Bean
    fun commentRepository(mongoCommentRepository: MongoCommentRepository) = MongoCommentRepositoryAdapter(
        mongoCommentRepository
    )

    @Bean
    fun instantNowSupplier(): InstantNowSupplier = InstantNowSupplierImpl()
}

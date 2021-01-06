package pl.lodz.p.stanczyk.articleservice.infrastructure.mongo

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoArticleRepository : MongoRepository<Article, String>

package pl.lodz.p.stanczyk.articleservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArticleServiceApplication

fun main(args: Array<String>) {
    runApplication<ArticleServiceApplication>(*args)
}

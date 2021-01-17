package pl.lodz.p.stanczyk.gatewayservice.adapter.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.lodz.p.stanczyk.gatewayservice.adapter.ServiceClientException

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(ServiceClientException::class)
    private fun handleServiceClientException(exception: ServiceClientException): ResponseEntity<String> =
        ResponseEntity.status(exception.status).contentType(MediaType.APPLICATION_JSON).body(exception.message ?: formatJsonMessage("An error has occurred"))

    private fun formatJsonMessage(message: String) = """
        {
            "message": "$message"
        }
    """.trimIndent()
}
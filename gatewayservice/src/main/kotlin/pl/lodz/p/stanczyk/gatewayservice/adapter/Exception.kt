package pl.lodz.p.stanczyk.gatewayservice.adapter

import java.lang.Exception

class ServiceClientException(message: String?, val status: Int): Exception(message)
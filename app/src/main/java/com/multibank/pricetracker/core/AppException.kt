package com.multibank.pricetracker.core

sealed class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    abstract val userMessage: String

    class HttpException(
        val code: Int,
        message: String
    ) : AppException("HTTP $code: $message") {
        override val userMessage: String get() = "Request failed: ${this.message ?: "Error $code"}"
    }

    class NetworkException(
        message: String,
        cause: Throwable
    ) : AppException(message, cause) {
        override val userMessage: String = "Network error. Check your connection."
    }

    class WebSocketConnectionFailed(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause) {
        override val userMessage: String get() = "Connection failed: ${this.message ?: "Unknown error"}"
    }

    class WebSocketMessageParseError(
        val raw: String
    ) : AppException("Cannot parse WebSocket message: \"$raw\"") {
        override val userMessage: String = "Invalid data received"
    }

    class WebSocketClosedUnexpectedly(
        val code: Int,
        val reason: String
    ) : AppException("WebSocket closed unexpectedly — code=$code reason=\"$reason\"") {
        override val userMessage: String get() = "Connection closed: $reason"
    }
}

fun Throwable.userMessage(): String = when (this) {
    is AppException -> userMessage
    else -> message ?: "Something went wrong"
}

package com.multibank.pricetracker.core

sealed class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class HttpException(
        val code: Int,
        message: String
    ) : AppException("HTTP $code: $message")

    /** A network-level I/O failure occurred while making an HTTP request. */
    class NetworkException(
        message: String,
        cause: Throwable
    ) : AppException(message, cause)

    // ── WebSocket ────────────────────────────────────────────────────────────

    /** The WebSocket handshake / TCP connection failed. */
    class WebSocketConnectionFailed(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause)

    /**
     * A WebSocket message could not be parsed into the expected format.
     * @param raw the original raw string that was rejected.
     */
    class WebSocketMessageParseError(
        val raw: String
    ) : AppException("Cannot parse WebSocket message: \"$raw\"")

    /**
     * The WebSocket connection was closed with a non-normal close code.
     * Code 1000 is a clean close; any other code is considered unexpected.
     */
    class WebSocketClosedUnexpectedly(
        val code: Int,
        val reason: String
    ) : AppException("WebSocket closed unexpectedly — code=$code reason=\"$reason\"")
}

/**
 * Returns a short, user-friendly message suitable for toasts or snackbars.
 */
fun Throwable.userMessage(): String = when (this) {
    is AppException.HttpException -> "Request failed: ${message ?: "Error $code"}"
    is AppException.NetworkException -> "Network error. Check your connection."
    is AppException.WebSocketConnectionFailed -> "Connection failed: ${message ?: "Unknown error"}"
    is AppException.WebSocketMessageParseError -> "Invalid data received"
    is AppException.WebSocketClosedUnexpectedly -> "Connection closed: $reason"
    else -> message ?: "Something went wrong"
}

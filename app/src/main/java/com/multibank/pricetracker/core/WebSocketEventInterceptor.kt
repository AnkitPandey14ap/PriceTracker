package com.multibank.pricetracker.core

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import javax.inject.Inject

private const val TAG = "WsEventInterceptor"

/**
 * Intercepts raw WebSocket lifecycle events.
 *
 * Each method mirrors the corresponding [okhttp3.WebSocketListener] callback.
 * Implementations may:
 *  - validate data and throw [AppException] subtypes
 *  - enrich / transform data (e.g. [interceptMessage] returns the validated text)
 *  - perform side-effects such as logging or metrics
 *
 * The [WebSocketService] calls these methods before acting on each event, so
 * any [AppException] thrown here is automatically caught and routed to the
 * error flow exposed by the service.
 */
interface WebSocketEventInterceptor {

    /** Called when the connection is established. */
    fun interceptOpen(ws: WebSocket, response: Response)

    /**
     * Called for every incoming text frame.
     * @return the validated/transformed text to forward downstream.
     * @throws [AppException.WebSocketMessageParseError] if the message is invalid.
     */
    fun interceptMessage(ws: WebSocket, text: String): String

    /**
     * Called when the connection fails.
     * @throws [AppException.WebSocketConnectionFailed] (or a subtype) typically.
     */
    fun interceptFailure(ws: WebSocket, t: Throwable, response: Response?)

    /**
     * Called when the peer closes the connection.
     * @throws [AppException.WebSocketClosedUnexpectedly] for non-normal closes.
     */
    fun interceptClosed(ws: WebSocket, code: Int, reason: String)
}

// ─────────────────────────────────────────────────────────────────────────────

/**
 * Default production implementation of [WebSocketEventInterceptor].
 *
 * Message validation rules:
 *  - Must not be blank
 *  - Must follow the `SYMBOL:PRICE` format (exactly two colon-separated parts)
 *  - The price part must be parseable as a [Double]
 *
 * Close codes:
 *  - 1000 = normal closure → no error
 *  - anything else         → [AppException.WebSocketClosedUnexpectedly]
 */
class DefaultWebSocketEventInterceptor @Inject constructor() : WebSocketEventInterceptor {

    override fun interceptOpen(ws: WebSocket, response: Response) {
        Log.d(TAG, "WebSocket opened — ${response.code}")
    }

    override fun interceptMessage(ws: WebSocket, text: String): String {
        if (text.isBlank()) {
            throw AppException.WebSocketMessageParseError(text)
        }

        val parts = text.trim().split(":")
        if (parts.size != 2 || parts[1].trim().toDoubleOrNull() == null) {
            throw AppException.WebSocketMessageParseError(text)
        }

        return text
    }

    override fun interceptFailure(ws: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "WebSocket failure — ${t.message}", t)
        throw AppException.WebSocketConnectionFailed(
            message = t.message ?: "WebSocket connection failed",
            cause = t
        )
    }

    override fun interceptClosed(ws: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "WebSocket closed — code=$code reason=$reason")
        if (code != 1000) {
            throw AppException.WebSocketClosedUnexpectedly(code, reason)
        }
    }
}

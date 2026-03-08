package com.multibank.pricetracker.core

import okhttp3.Response
import okhttp3.WebSocket

interface WebSocketEventInterceptor {

    fun interceptOpen(ws: WebSocket, response: Response)

    fun interceptMessage(ws: WebSocket, text: String): String

    fun interceptFailure(ws: WebSocket, t: Throwable, response: Response?)

    fun interceptClosed(ws: WebSocket, code: Int, reason: String)
}

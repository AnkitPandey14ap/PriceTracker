package com.multibank.pricetracker.data.core

import org.junit.Assert.assertEquals
import org.junit.Test

class AppExceptionUserMessageTest {

    @Test
    fun httpException_userMessage_returnsRequestFailed() {
        val e = AppException.HttpException(404, "Not Found")

        assertEquals("Request failed: HTTP 404: Not Found", e.userMessage)
    }

    @Test
    fun networkException_userMessage_returnsNetworkError() {
        val e = AppException.NetworkException("timeout", RuntimeException("cause"))

        assertEquals("Network error. Check your connection.", e.userMessage)
    }

    @Test
    fun webSocketConnectionFailed_userMessage_returnsConnectionFailed() {
        val e = AppException.WebSocketConnectionFailed("Connection refused")

        assertEquals("Connection failed: Connection refused", e.userMessage)
    }

    @Test
    fun webSocketMessageParseError_userMessage_returnsInvalidData() {
        val e = AppException.WebSocketMessageParseError("bad")

        assertEquals("Invalid data received", e.userMessage)
    }

    @Test
    fun webSocketClosedUnexpectedly_userMessage_returnsConnectionClosed() {
        val e = AppException.WebSocketClosedUnexpectedly(1006, "Abnormal closure")

        assertEquals("Connection closed: Abnormal closure", e.userMessage)
    }

    @Test
    fun throwable_userMessageExtension_whenAppException_returnsUserMessage() {
        val e = AppException.HttpException(500, "Server Error")

        assertEquals("Request failed: HTTP 500: Server Error", e.userMessage())
    }

    @Test
    fun throwable_userMessageExtension_whenGenericThrowable_returnsMessageOrDefault() {
        val e = RuntimeException("Something broke")

        assertEquals("Something broke", e.userMessage())
    }

    @Test
    fun throwable_userMessageExtension_whenNullMessage_returnsSomethingWentWrong() {
        val e = RuntimeException()

        assertEquals("Something went wrong", e.userMessage())
    }
}

package com.multibank.pricetracker.data

import android.util.Log
import com.multibank.pricetracker.core.AppException
import com.multibank.pricetracker.core.userMessage
import com.multibank.pricetracker.core.WebSocketEventInterceptor
import com.multibank.pricetracker.domain.model.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val TAG = "WebSocketTransport"

@Singleton
class WebSocketTransport @Inject constructor(
    @param:Named("WebSocket") private val okHttpClient: OkHttpClient,
    private val eventInterceptor: WebSocketEventInterceptor
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _messages = MutableSharedFlow<String>(replay = 25)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    private val _errorMessages = MutableSharedFlow<String>(replay = 0)
    val errorMessages: SharedFlow<String> = _errorMessages.asSharedFlow()

    fun connect(url: String) {
        webSocket?.cancel()
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder().url(url).build()
        webSocket = okHttpClient.newWebSocket(request, TransportWebSocketListener())
    }

    private inner class TransportWebSocketListener : WebSocketListener() {

        override fun onOpen(ws: WebSocket, response: Response) {
            try {
                eventInterceptor.interceptOpen(ws, response)
                Log.d(TAG, "WebSocket connected")
                _connectionState.value = ConnectionState.Connected
            } catch (e: AppException) {
                Log.e(TAG, "WebSocket open interceptor error", e)
                _connectionState.value = ConnectionState.Disconnected
                scope.launch { _errorMessages.emit(e.userMessage()) }
            }
        }

        override fun onMessage(ws: WebSocket, text: String) {
            try {
                val validated = eventInterceptor.interceptMessage(ws, text)
                scope.launch {
                    _messages.emit(validated)
                }
            } catch (e: AppException) {
                Log.w(TAG, "WebSocket message rejected: ${e.message}")
            }
        }

        override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
            var userMsg: String? = null
            try {
                eventInterceptor.interceptFailure(ws, t, response)
            } catch (e: AppException) {
                Log.e(TAG, "WebSocket failure", e)
                userMsg = e.userMessage()
            }
            _connectionState.value = ConnectionState.Disconnected
            if (userMsg == null) userMsg = (t.cause ?: t).userMessage()
            scope.launch { _errorMessages.emit(userMsg) }
        }

        override fun onClosed(ws: WebSocket, code: Int, reason: String) {
            var userMsg: String? = null
            try {
                eventInterceptor.interceptClosed(ws, code, reason)
            } catch (e: AppException) {
                Log.w(TAG, "WebSocket closed unexpectedly: ${e.message}")
                userMsg = e.userMessage()
            }
            Log.d(TAG, "WebSocket closed: $reason")
            _connectionState.value = ConnectionState.Disconnected
            if (userMsg != null) scope.launch { _errorMessages.emit(userMsg) }
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Stopped by user")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun send(text: String) {
        webSocket?.send(text)
    }
}

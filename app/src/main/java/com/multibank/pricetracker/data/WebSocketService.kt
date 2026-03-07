package com.multibank.pricetracker.data


import android.util.Log
import com.multibank.pricetracker.ui.feed.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.random.Random

private const val TAG = "WebSocketService"
private const val WS_URL = "wss://ws.postman-echo.com/raw"
private const val PRICE_UPDATE_INTERVAL_MS = 1500L

@Singleton
class WebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var webSocket: WebSocket? = null
    private var reconnectJob: Job? = null

    private var isRunning = false

    private var subscribedSymbols: List<StockSymbol> = emptyList()

    private var sendJob: Job? = null

    private val currentPrices = mutableMapOf<String, Double>()


    private val _connectionState =
        MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    val connectionState: StateFlow<ConnectionState> =
        _connectionState.asStateFlow()

    private val _messages = MutableSharedFlow<String>(replay = 25)

    val messages: SharedFlow<String> =
        _messages.asSharedFlow()

    fun start(symbols: List<StockSymbol>) {

        if (isRunning) return

        isRunning = true
        subscribedSymbols = symbols

        symbols.forEach {
            currentPrices[it.symbol] = it.currentPrice
        }

        connect()
    }

    fun stop() {

        isRunning = false

        reconnectJob?.cancel()
        sendJob?.cancel()

        reconnectJob = null
        sendJob = null

        webSocket?.close(1000, "Stopped by user")
        webSocket = null

        _connectionState.value = ConnectionState.Disconnected
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    private fun connect() {
        webSocket?.cancel() // important cleanup
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url(WS_URL)
            .build()

        webSocket = okHttpClient.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(ws: WebSocket, response: Response) {

                    Log.d(TAG, "WebSocket connected")

                    _connectionState.value = ConnectionState.Connected

                    startSending(ws, subscribedSymbols)
                }

                override fun onMessage(ws: WebSocket, text: String) {

                    scope.launch {
                        _messages.emit(text)
                    }
                }

                override fun onFailure(
                    ws: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {

                    Log.e(TAG, "WebSocket failure", t)

                    _connectionState.value = ConnectionState.Disconnected

                    if (isRunning) {
                        scheduleReconnect()
                    }
                }

                override fun onClosed(
                    ws: WebSocket,
                    code: Int,
                    reason: String
                ) {

                    Log.d(TAG, "WebSocket closed: $reason")

                    _connectionState.value = ConnectionState.Disconnected

                    if (isRunning) {
                        scheduleReconnect()
                    }
                }
            }
        )
    }

    private fun scheduleReconnect() {

        reconnectJob?.cancel()

        reconnectJob = scope.launch {

            delay(3000)

            if (isRunning) {
                Log.d(TAG, "Reconnecting WebSocket")
                connect()
            }
        }
    }

    private fun startSending(ws: WebSocket, symbols: List<StockSymbol>) {

        sendJob?.cancel()

        sendJob = scope.launch {

            while (isRunning) {

                symbols.forEach { stock ->

                    val oldPrice =
                        currentPrices[stock.symbol] ?: stock.currentPrice

                    val changePercent =
                        Random.nextDouble(-2.0, 2.0)

                    val newPrice =
                        (oldPrice * (1 + changePercent / 100.0))
                            .coerceAtLeast(1.0)

                    val rounded =
                        (newPrice * 100.0).roundToInt() / 100.0

                    currentPrices[stock.symbol] = rounded

                    val message = "${stock.symbol}:$rounded"

                    ws.send(message)
                }

                delay(PRICE_UPDATE_INTERVAL_MS)
            }
        }
    }
}
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
import kotlin.random.Random

private const val TAG = "WebSocketRepository"
private const val WS_URL = "wss://ws.postman-echo.com/raw"
private const val PRICE_UPDATE_INTERVAL_MS = 2000L

@Singleton
class WebSocketRepository @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(replay = 25)
    val priceUpdates: SharedFlow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private var webSocket: WebSocket? = null
    private var sendJob: Job? = null
    private var isRunning = false

    /** True when the feed has been started by the user and is actively streaming. */
    val isActive: Boolean get() = isRunning

    // Current prices kept in repo so we deterministically generate realistic deltas
    private val currentPrices = mutableMapOf<String, Double>()

    fun start(symbols: List<StockSymbol>) {
        if (isRunning) return
        isRunning = true

        // Seed prices
        symbols.forEach { currentPrices[it.symbol] = it.currentPrice }

        connect(symbols)
    }

    fun stop() {
        isRunning = false
        sendJob?.cancel()
        sendJob = null
        webSocket?.close(1000, "User stopped feed")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    private fun connect(symbols: List<StockSymbol>) {
        _connectionState.value = ConnectionState.Connecting
        val request = Request.Builder().url(WS_URL).build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                _connectionState.value = ConnectionState.Connected
                startSending(ws, symbols)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                parseEchoMessage(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                _connectionState.value = ConnectionState.Disconnected
                if (isRunning) {
                    // Reconnect after delay
                    scope.launch {
                        delay(3000)
                        if (isRunning) connect(symbols)
                    }
                }
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $reason")
                _connectionState.value = ConnectionState.Disconnected
            }
        })
    }

    private fun startSending(ws: WebSocket, symbols: List<StockSymbol>) {
        sendJob?.cancel()
        sendJob = scope.launch {
            while (isRunning) {
                symbols.forEach { stock ->
                    val oldPrice = currentPrices[stock.symbol] ?: stock.currentPrice
                    // Generate ±0.5% to ±2% random change
                    val changePercent = Random.nextDouble(-2.0, 2.0)
                    val newPrice = (oldPrice * (1 + changePercent / 100.0)).coerceAtLeast(1.0)
                    val rounded = Math.round(newPrice * 100.0) / 100.0
                    currentPrices[stock.symbol] = rounded

                    val message = "${stock.symbol}:$rounded"
                    ws.send(message)
                }
                delay(PRICE_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun parseEchoMessage(text: String) {
        try {
            val parts = text.trim().split(":")
            if (parts.size == 2) {
                val symbol = parts[0].trim()
                val price = parts[1].trim().toDoubleOrNull() ?: return
                scope.launch {
                    _priceUpdates.emit(PriceUpdate(symbol = symbol, price = price))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing echo message: $text", e)
        }
    }
}

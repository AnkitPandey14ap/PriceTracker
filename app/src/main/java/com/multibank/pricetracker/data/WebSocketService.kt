package com.multibank.pricetracker.data

import com.multibank.pricetracker.domain.model.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val WS_URL = "wss://ws.postman-echo.com/raw"

/**
 * Orchestrates WebSocket connection and simulated price feed.
 * Delegates to [WebSocketTransport] and [PriceFeedSimulator].
 *
 * - Connects once; no automatic retry. On failure or timeout, emits to [errorMessages] (first only) and stops.
 * - Resets "first failure" flag when [ConnectionState] becomes Connected or on stop().
 */
@Singleton
class WebSocketService @Inject constructor(
    private val transport: WebSocketTransport,
    private val simulator: PriceFeedSimulator
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isRunning = false
    private var simulatorJob: Job? = null
    private var connectionObserverJob: Job? = null
    private var errorForwarderJob: Job? = null

    private var hasEmittedErrorThisRun = false

    private val _userErrorMessages = MutableSharedFlow<String>(replay = 0)
    /** User-facing errors: only the first failure per run (resets on Connected or stop()). */
    val errorMessages: SharedFlow<String> = _userErrorMessages.asSharedFlow()

    val connectionState: StateFlow<ConnectionState> = transport.connectionState
    val messages: SharedFlow<String> = transport.messages

    fun start(symbols: List<StockSymbolDto>) {
        if (isRunning) return
        isRunning = true
        hasEmittedErrorThisRun = false

        transport.connect(WS_URL)
        simulatorJob = scope.launch {
            simulator.run(symbols).collect { message ->
                transport.send(message)
            }
        }
        errorForwarderJob = scope.launch {
            transport.errorMessages.collect { msg ->
                if (!hasEmittedErrorThisRun) {
                    _userErrorMessages.emit(msg)
                    hasEmittedErrorThisRun = true
                }
            }
        }
        connectionObserverJob = scope.launch {
            transport.connectionState.collect { state ->
                when (state) {
                    is ConnectionState.Connected -> hasEmittedErrorThisRun = false
                    is ConnectionState.Disconnected -> { /* no retry */ }
                    is ConnectionState.Connecting -> { }
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        hasEmittedErrorThisRun = false
        errorForwarderJob?.cancel()
        errorForwarderJob = null
        connectionObserverJob?.cancel()
        connectionObserverJob = null
        simulatorJob?.cancel()
        simulatorJob = null
        transport.disconnect()
    }
}

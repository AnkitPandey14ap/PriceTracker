package com.multibank.pricetracker.data.datasource.websocket

import com.multibank.pricetracker.data.datasource.PriceFeedDataSource
import com.multibank.pricetracker.data.datasource.PriceMessageParser
import com.multibank.pricetracker.data.mapper.PriceUpdateMapper
import com.multibank.pricetracker.data.mapper.StockSymbolMapper
import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val WS_URL = "wss://ws.postman-echo.com/raw"

/**
 * WebSocket-based implementation of [PriceFeedDataSource].
 * Orchestrates [WebSocketTransport] and [PriceFeedSimulator].
 *
 * - Connects once; no automatic retry. On failure or timeout, emits to [errorMessages] (first only) and stops.
 * - Resets "first failure" flag when [ConnectionStateEntity] becomes Connected or on stop().
 */
@Singleton
class WebSocketService @Inject constructor(
    private val transport: WebSocketTransport,
    private val simulator: PriceFeedSimulator,
    private val messageParser: PriceMessageParser
) : PriceFeedDataSource {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isRunning = false
    private var simulatorJob: Job? = null
    private var connectionObserverJob: Job? = null
    private var errorForwarderJob: Job? = null

    private var hasEmittedErrorThisRun = false

    private val _userErrorMessages = MutableSharedFlow<String>(replay = 0)

    override val connectionState: StateFlow<ConnectionStateEntity> = transport.connectionState

    override val priceUpdates: Flow<PriceUpdateEntity> =
        transport.messages
            .mapNotNull { messageParser.parse(it) }
            .map { PriceUpdateMapper.toDomain(it) }

    /** User-facing errors: only the first failure per run (resets on Connected or stop()). */
    override val errorMessages: SharedFlow<String> = _userErrorMessages.asSharedFlow()

    override fun start(symbols: List<StockSymbolEntity>) {
        if (isRunning) return
        isRunning = true
        hasEmittedErrorThisRun = false

        transport.connect(WS_URL)
        val symbolsDto = symbols.map { StockSymbolMapper.toDto(it) }
        simulatorJob = scope.launch {
            simulator.run(symbolsDto).collect { message ->
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
                    is ConnectionStateEntity.Connected -> hasEmittedErrorThisRun = false
                    is ConnectionStateEntity.Disconnected -> { /* no retry */ }
                    is ConnectionStateEntity.Connecting -> { }
                }
            }
        }
    }

    override fun stop() {
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

package com.multibank.pricetracker.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.pricetracker.data.PriceDirection
import com.multibank.pricetracker.data.StockSymbol
import com.multibank.pricetracker.data.WebSocketRepository
import com.multibank.pricetracker.data.buildInitialStocks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FLASH_THRESHOLD_PERCENT = 1.0


@HiltViewModel
class FeedVM @Inject constructor(
    private val repository: WebSocketRepository
) : ViewModel() {

    private val flashJobs = mutableMapOf<String, Job>()

    private val initialStocks = buildInitialStocks()
    private val stockMap = mutableMapOf<String, StockSymbol>()
        .also { map -> initialStocks.forEach { map[it.symbol] = it } }


    private val _stocksFlow = MutableStateFlow(initialStocks)
    private val _feedIntent = MutableSharedFlow<FeedIntent>()
    private val _feedSideEffect = MutableSharedFlow<FeedSideEffect>()
    val feedSideEffect = _feedSideEffect.asSharedFlow()


    private val _flashMap = MutableStateFlow<Map<String, Boolean?>>(emptyMap())
    private val _isFeedRunning = MutableStateFlow(false)

    val uiState: StateFlow<FeedUiState> = combine(
        _stocksFlow,
        repository.connectionState,
        _isFeedRunning,
        _flashMap
    ) { stocks, connectionState, isRunning, flashMap ->
        FeedUiState(
            stocks = stocks.sortedByDescending { it.currentPrice },
            connectionState = connectionState,
            isFeedRunning = isRunning,
            flashMap = flashMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedUiState(stocks = initialStocks.sortedByDescending { it.currentPrice })
    )

    init {
        observePriceUpdates()
    }
    fun sendIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.SymbolClicked -> _feedSideEffect.tryEmit(
                FeedSideEffect.NavigateToDetailPage(
                    intent.id
                )
            )

            FeedIntent.ToggleConnection -> toggleFeed()
        }
    }

    private fun toggleFeed() {
        if (_isFeedRunning.value) {
            repository.stop()
            _isFeedRunning.value = false
        } else {
            repository.start(initialStocks)
            _isFeedRunning.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashJobs.values.forEach { it.cancel() }
    }

    private fun observePriceUpdates() {
        viewModelScope.launch {
            repository.priceUpdates.collect { update ->
                val existing = stockMap[update.symbol] ?: return@collect
                val direction = when {
                    update.price > existing.currentPrice -> PriceDirection.UP
                    update.price < existing.currentPrice -> PriceDirection.DOWN
                    else -> PriceDirection.NEUTRAL
                }
                val updated = existing.copy(
                    previousPrice = existing.currentPrice,
                    currentPrice = update.price,
                    direction = direction
                )
                stockMap[update.symbol] = updated
                _stocksFlow.value = stockMap.values.toList()

                // Only flash if the change is significant (>= 1%)
                val changePct = if (existing.currentPrice > 0)
                    kotlin.math.abs((update.price - existing.currentPrice) / existing.currentPrice * 100.0)
                else 0.0
                if (direction != PriceDirection.NEUTRAL && changePct >= FLASH_THRESHOLD_PERCENT) {
                    triggerFlash(update.symbol, direction == PriceDirection.UP)
                }
            }
        }
    }

    private fun triggerFlash(symbol: String, isUp: Boolean) {
        flashJobs[symbol]?.cancel()
        flashJobs[symbol] = viewModelScope.launch {
            _flashMap.update { it + (symbol to isUp) }
            delay(1000)
            _flashMap.update { it + (symbol to null) }
        }
    }


}
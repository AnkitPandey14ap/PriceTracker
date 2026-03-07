package com.multibank.pricetracker.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.pricetracker.Constants.Companion.FLASH_THRESHOLD_PERCENT
import com.multibank.pricetracker.data.PriceDirection
import com.multibank.pricetracker.data.PriceUpdate
import com.multibank.pricetracker.data.buildInitialStocks
import com.multibank.pricetracker.domain.ObserveConnectionStateUseCase
import com.multibank.pricetracker.domain.ObservePriceUpdatesUseCase
import com.multibank.pricetracker.domain.StartFeedUseCase
import com.multibank.pricetracker.domain.StopFeedUseCase
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


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val startFeedUseCase: StartFeedUseCase,
    private val stopFeedUseCase: StopFeedUseCase,
    private val observePriceUpdatesUseCase: ObservePriceUpdatesUseCase,
    private val observeConnectionStateUseCase: ObserveConnectionStateUseCase
) : ViewModel() {

    private val flashJobs = mutableMapOf<String, Job>()

    private val initialStocks = buildInitialStocks()
    private val stockMap = initialStocks.associateBy { it.symbol }.toMutableMap()


    private val _stocksFlow = MutableStateFlow(initialStocks)
    private val _feedSideEffect = MutableSharedFlow<FeedSideEffect>()
    val feedSideEffect = _feedSideEffect.asSharedFlow()


    private val _flashMap = MutableStateFlow<Map<String, Boolean?>>(emptyMap())
    private val _isFeedRunning = MutableStateFlow(false)

    val uiState: StateFlow<FeedUiState> = combine(
        _stocksFlow,
        observeConnectionStateUseCase(),
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
                FeedSideEffect.NavigateToDetailPage(intent.id)
            )
            FeedIntent.ToggleConnection -> toggleFeed()
        }
    }

    private fun toggleFeed() {
        val running = _isFeedRunning.value

        if (running) stopFeedUseCase()
        else startFeedUseCase(initialStocks)

        _isFeedRunning.value = !running
    }

    private fun observePriceUpdates() {
        viewModelScope.launch {
            observePriceUpdatesUseCase().collect { update ->
                processPriceUpdate(update)
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

    private fun processPriceUpdate(update: PriceUpdate) {
        val existing = stockMap[update.symbol] ?: return

        val direction = PriceUtil.priceDirection(update.price, existing.currentPrice)

        val updated = existing.copy(
            previousPrice = existing.currentPrice,
            currentPrice = update.price,
            direction = direction
        )

        stockMap[update.symbol] = updated
        _stocksFlow.value = stockMap.values.toList()

        val changePct = PriceUtil.priceChangePercent(update.price, existing.currentPrice)

        if (direction != PriceDirection.NEUTRAL &&
            changePct >= FLASH_THRESHOLD_PERCENT
        ) {
            triggerFlash(update.symbol, direction == PriceDirection.UP)
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashJobs.values.forEach { it.cancel() }
    }


}
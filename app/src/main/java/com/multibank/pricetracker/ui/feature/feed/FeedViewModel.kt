package com.multibank.pricetracker.ui.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.pricetracker.ui.common.Constants.Companion.FLASH_THRESHOLD_PERCENT
import com.multibank.pricetracker.domain.usecase.GetInitialSymbolsUseCase
import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import com.multibank.pricetracker.domain.usecase.ObserveConnectionStateUseCase
import com.multibank.pricetracker.domain.usecase.ObservePriceFeedErrorsUseCase
import com.multibank.pricetracker.domain.usecase.ObservePriceUpdatesUseCase
import com.multibank.pricetracker.domain.usecase.StartFeedUseCase
import com.multibank.pricetracker.domain.usecase.StopFeedUseCase
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedIntent
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedSideEffect
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedUiState
import com.multibank.pricetracker.ui.util.PriceUtil
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
import kotlin.reflect.KClass
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getInitialSymbolsUseCase: GetInitialSymbolsUseCase,
    private val startFeedUseCase: StartFeedUseCase,
    private val stopFeedUseCase: StopFeedUseCase,
    private val observePriceUpdatesUseCase: ObservePriceUpdatesUseCase,
    private val observeConnectionStateUseCase: ObserveConnectionStateUseCase,
    private val observePriceFeedErrorsUseCase: ObservePriceFeedErrorsUseCase
) : ViewModel() {

    private val flashJobs = mutableMapOf<String, Job>()

    private val initialStocks: List<StockSymbolEntity> = getInitialSymbolsUseCase()
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
            stocks = stocks.sortedByDescending { it.currentPrice }
                .map { with(FeedMapper) { it.toFeedItemUi() } },
            connectionState = with(FeedMapper) { connectionState.toConnectionStateUi() },
            isFeedRunning = isRunning,
            flashMap = flashMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedUiState(
            stocks = initialStocks.sortedByDescending { it.currentPrice }
                .map { with(FeedMapper) { it.toFeedItemUi() } }
        )
    )

    private val intentHandlers: Map<KClass<out FeedIntent>, (FeedIntent) -> Unit> = mapOf(
        FeedIntent.SymbolClicked::class to { e ->
            _feedSideEffect.tryEmit(FeedSideEffect.NavigateToDetailPage((e as FeedIntent.SymbolClicked).id))
        },
        FeedIntent.ToggleConnection::class to { _ -> toggleFeed() }
    )

    init {
        observePriceUpdates()
        observeErrors()
    }

    private fun observeErrors() {
        viewModelScope.launch {
            observePriceFeedErrorsUseCase().collect { message ->
                _feedSideEffect.emit(FeedSideEffect.ShowToast(message))
                stopFeedUseCase()
                _isFeedRunning.value = false
            }
        }
    }

    fun sendIntent(intent: FeedIntent) {
        intentHandlers[intent::class]?.invoke(intent)
            ?: error("No handler registered for intent: ${intent::class.simpleName}")
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

    private fun processPriceUpdate(update: PriceUpdateEntity) {
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
        if (direction != PriceDirectionEntity.NEUTRAL && changePct >= FLASH_THRESHOLD_PERCENT) {
            triggerFlash(update.symbol, direction == PriceDirectionEntity.UP)
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashJobs.values.forEach { it.cancel() }
    }
}

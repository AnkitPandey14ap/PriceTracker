package com.multibank.pricetracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.pricetracker.Constants.Companion.FLASH_THRESHOLD_PERCENT
import com.multibank.pricetracker.domain.GetInitialSymbolsUseCase
import com.multibank.pricetracker.domain.model.PriceDirection
import com.multibank.pricetracker.domain.model.StockSymbol
import com.multibank.pricetracker.domain.ObserveConnectionStateUseCase
import com.multibank.pricetracker.domain.ObservePriceUpdatesUseCase
import com.multibank.pricetracker.ui.feed.mapper.FeedMapper
import com.multibank.pricetracker.ui.feed.model.ConnectionStateUi
import com.multibank.pricetracker.ui.feed.model.FeedItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

data class DetailUiState(
    val stock: FeedItemUi? = null,
    val flash: Boolean? = null,
    val connectionState: ConnectionStateUi = ConnectionStateUi.Disconnected
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getInitialSymbolsUseCase: GetInitialSymbolsUseCase,
    private val observePriceUpdatesUseCase: ObservePriceUpdatesUseCase,
    observeConnectionStateUseCase: ObserveConnectionStateUseCase
) : ViewModel() {

    private val symbol: String = checkNotNull(savedStateHandle["symbol"])

    private val initialStock: StockSymbol = getInitialSymbolsUseCase().first { it.symbol == symbol }
    private val _stock = MutableStateFlow(initialStock)
    private val _flash = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<DetailUiState> = combine(
        _stock,
        _flash,
        observeConnectionStateUseCase()
    ) { stock, flash, connectionState ->
        DetailUiState(
            stock = with(FeedMapper) { stock.toFeedItemUi() },
            flash = flash,
            connectionState = with(FeedMapper) { connectionState.toConnectionStateUi() }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailUiState(stock = with(FeedMapper) { initialStock.toFeedItemUi() })
    )

    private var flashJob: Job? = null

    init {
        observeUpdates()
    }

    private fun observeUpdates() {
        viewModelScope.launch {
            observePriceUpdatesUseCase().collect { update ->
                if (update.symbol != symbol) return@collect
                val existing = _stock.value
                val direction = when {
                    update.price > existing.currentPrice -> PriceDirection.UP
                    update.price < existing.currentPrice -> PriceDirection.DOWN
                    else -> PriceDirection.NEUTRAL
                }
                _stock.value = existing.copy(
                    previousPrice = existing.currentPrice,
                    currentPrice = update.price,
                    direction = direction
                )
                val changePct = if (existing.currentPrice > 0)
                    abs((update.price - existing.currentPrice) / existing.currentPrice * 100.0)
                else 0.0
                if (direction != PriceDirection.NEUTRAL && changePct >= FLASH_THRESHOLD_PERCENT) {
                    triggerFlash(direction == PriceDirection.UP)
                }
            }
        }
    }

    private fun triggerFlash(isUp: Boolean) {
        flashJob?.cancel()
        flashJob = viewModelScope.launch {
            _flash.value = isUp
            delay(1000)
            _flash.value = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashJob?.cancel()
    }
}

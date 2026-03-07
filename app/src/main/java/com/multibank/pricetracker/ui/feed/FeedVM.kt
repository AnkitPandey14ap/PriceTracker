package com.multibank.pricetracker.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multibank.pricetracker.data.StockSymbol
import com.multibank.pricetracker.data.buildInitialStocks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class FeedVM @Inject constructor() : ViewModel() {

    private val initialStocks = buildInitialStocks()
    private val stockMap = mutableMapOf<String, StockSymbol>()
        .also { map -> initialStocks.forEach { map[it.symbol] = it } }


    private val _stocksFlow = MutableStateFlow(initialStocks)
    private val _feedIntent = MutableSharedFlow<FeedIntent>()
    private val _feedSideEffect = MutableSharedFlow<FeedSideEffect>()
    val feedSideEffect = _feedSideEffect.asSharedFlow()


    private val _flashMap = MutableStateFlow<Map<String, Boolean?>>(emptyMap())
    private val _isFeedRunning = MutableStateFlow(false)

    //TODO
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    val uiState: StateFlow<FeedUiState> = combine(
        _stocksFlow,
        //TODO
        _connectionState,
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

    private fun toggleFeed() {
        TODO()
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

}
package com.multibank.pricetracker.ui.feature.feed

import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedIntent
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedSideEffect
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [FeedViewModelContract] for Compose UI tests. Holds configurable state and records intents.
 */
class FakeFeedViewModel(
    initialState: FeedUiState = FeedUiState(
        stocks = listOf(
            FeedItemUi("AAPL", 150.0, 148.0, PriceDirectionUi.UP, "Apple Inc."),
            FeedItemUi("GOOG", 140.0, 138.0, PriceDirectionUi.DOWN, "Alphabet Inc.")
        ),
        connectionState = ConnectionStateUi.Connected,
        isFeedRunning = false
    )
) : FeedViewModelContract {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _feedSideEffect = MutableSharedFlow<FeedSideEffect>()
    override val feedSideEffect: SharedFlow<FeedSideEffect> = _feedSideEffect.asSharedFlow()

    val recordedIntents = mutableListOf<FeedIntent>()

    fun setState(state: FeedUiState) {
        _uiState.value = state
    }

    override fun sendIntent(intent: FeedIntent) {
        recordedIntents.add(intent)
    }
}

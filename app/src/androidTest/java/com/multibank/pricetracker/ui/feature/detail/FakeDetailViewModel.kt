package com.multibank.pricetracker.ui.feature.detail

import com.multibank.pricetracker.ui.feature.detail.mvi.DetailIntent
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailSideEffect
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailUiState
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [DetailViewModelContract] for Compose UI tests. Holds configurable state and records intents.
 */
class FakeDetailViewModel(
    initialState: DetailUiState = DetailUiState(
        stock = FeedItemUi("AAPL", 150.50, 148.25, PriceDirectionUi.UP, "Apple Inc. — global technology company."),
        flash = null,
        connectionState = ConnectionStateUi.Connected
    )
) : DetailViewModelContract {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _detailSideEffect = MutableSharedFlow<DetailSideEffect>()
    override val detailSideEffect: SharedFlow<DetailSideEffect> = _detailSideEffect.asSharedFlow()

    val recordedIntents = mutableListOf<DetailIntent>()

    fun setState(state: DetailUiState) {
        _uiState.value = state
    }

    override fun sendIntent(intent: DetailIntent) {
        recordedIntents.add(intent)
    }
}

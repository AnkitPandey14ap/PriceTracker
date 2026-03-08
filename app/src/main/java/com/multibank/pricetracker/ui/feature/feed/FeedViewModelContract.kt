package com.multibank.pricetracker.ui.feature.feed

import com.multibank.pricetracker.ui.feature.feed.mvi.FeedIntent
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedSideEffect
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedUiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for the Feed screen ViewModel. Allows replacing the real ViewModel with a fake in tests.
 */
interface FeedViewModelContract {
    val uiState: StateFlow<FeedUiState>
    val feedSideEffect: SharedFlow<FeedSideEffect>
    fun sendIntent(intent: FeedIntent)
}

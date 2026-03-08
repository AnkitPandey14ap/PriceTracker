package com.multibank.pricetracker.ui.feature.detail

import com.multibank.pricetracker.ui.feature.detail.mvi.DetailIntent
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailSideEffect
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailUiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for the Detail screen ViewModel. Allows replacing the real ViewModel with a fake in tests.
 */
interface DetailViewModelContract {
    val uiState: StateFlow<DetailUiState>
    val detailSideEffect: SharedFlow<DetailSideEffect>
    fun sendIntent(intent: DetailIntent)
}

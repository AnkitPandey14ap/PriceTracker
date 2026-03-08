package com.multibank.pricetracker.ui.feature.detail.mvi

sealed class DetailSideEffect {
    object NavigateBack : DetailSideEffect()
}

package com.multibank.pricetracker.ui.feature.detail.mvi

sealed class DetailIntent {
    object NavigateBack : DetailIntent()
}
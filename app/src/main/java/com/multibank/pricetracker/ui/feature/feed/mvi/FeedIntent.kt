package com.multibank.pricetracker.ui.feature.feed.mvi

sealed class FeedIntent {
    data class SymbolClicked(val symbol: String) : FeedIntent()
    object ToggleConnection : FeedIntent()
}
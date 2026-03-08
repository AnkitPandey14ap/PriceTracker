package com.multibank.pricetracker.ui.feature.feed.mvi

sealed class FeedIntent {
    data class SymbolClicked(val id: String) : FeedIntent()
    object ToggleConnection : FeedIntent()
}
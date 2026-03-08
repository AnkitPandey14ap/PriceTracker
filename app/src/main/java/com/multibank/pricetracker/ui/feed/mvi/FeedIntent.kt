package com.multibank.pricetracker.ui.feed.mvi

sealed class FeedIntent {
    data class SymbolClicked(val id: String) : FeedIntent()
    object ToggleConnection : FeedIntent()
}
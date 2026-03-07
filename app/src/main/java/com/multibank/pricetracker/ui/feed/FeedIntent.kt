package com.multibank.pricetracker.ui.feed

sealed class FeedIntent {
    data class SymbolClicked(val id: String) : FeedIntent()
    object ToggleConnection : FeedIntent()
}
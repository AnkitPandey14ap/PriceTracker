package com.multibank.pricetracker.ui.feed

sealed class FeedSideEffect {
    data class NavigateToDetailPage(val id: String) : FeedSideEffect()
    data class ShowToast(val text: String) : FeedSideEffect()
}
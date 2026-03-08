package com.multibank.pricetracker.ui.feature.feed.mvi

sealed class FeedSideEffect {
    data class NavigateToDetailPage(val id: String) : FeedSideEffect()
    data class ShowToast(val text: String) : FeedSideEffect()
}
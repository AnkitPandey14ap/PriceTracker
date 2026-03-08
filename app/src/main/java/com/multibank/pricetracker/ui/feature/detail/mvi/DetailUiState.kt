package com.multibank.pricetracker.ui.feature.detail.mvi

import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi

data class DetailUiState(
    val stock: FeedItemUi? = null,
    val flash: Boolean? = null,
    val connectionState: ConnectionStateUi = ConnectionStateUi.Disconnected
)

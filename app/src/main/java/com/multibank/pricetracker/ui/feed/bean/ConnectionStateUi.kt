package com.multibank.pricetracker.ui.feed.bean

sealed class ConnectionStateUi {
    data object Connected : ConnectionStateUi()
    data object Disconnected : ConnectionStateUi()
    data object Connecting : ConnectionStateUi()
}

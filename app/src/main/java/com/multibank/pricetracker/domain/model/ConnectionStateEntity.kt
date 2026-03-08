package com.multibank.pricetracker.domain.model

sealed class ConnectionStateEntity {
    data object Connected : ConnectionStateEntity()
    data object Disconnected : ConnectionStateEntity()
    data object Connecting : ConnectionStateEntity()
}

package com.multibank.pricetracker.ui.feed.mapper

import com.multibank.pricetracker.domain.model.ConnectionState
import com.multibank.pricetracker.domain.model.PriceDirection
import com.multibank.pricetracker.domain.model.StockSymbol
import com.multibank.pricetracker.ui.feed.model.ConnectionStateUi
import com.multibank.pricetracker.ui.feed.model.FeedItemUi
import com.multibank.pricetracker.ui.feed.model.PriceDirectionUi

object FeedMapper {

    fun StockSymbol.toFeedItemUi(): FeedItemUi =
        FeedItemUi(
            symbol = symbol,
            currentPrice = currentPrice,
            previousPrice = previousPrice,
            direction = direction.toUi(),
            description = description
        )

    fun ConnectionState.toConnectionStateUi(): ConnectionStateUi = when (this) {
        is ConnectionState.Connected -> ConnectionStateUi.Connected
        is ConnectionState.Disconnected -> ConnectionStateUi.Disconnected
        is ConnectionState.Connecting -> ConnectionStateUi.Connecting
    }

    private fun PriceDirection.toUi(): PriceDirectionUi = when (this) {
        PriceDirection.UP -> PriceDirectionUi.UP
        PriceDirection.DOWN -> PriceDirectionUi.DOWN
        PriceDirection.NEUTRAL -> PriceDirectionUi.NEUTRAL
    }
}

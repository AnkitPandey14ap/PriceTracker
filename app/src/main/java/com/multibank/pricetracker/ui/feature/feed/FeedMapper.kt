package com.multibank.pricetracker.ui.feature.feed

import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi

object FeedMapper {

    fun StockSymbolEntity.toFeedItemUi(): FeedItemUi =
        FeedItemUi(
            symbol = symbol,
            currentPrice = currentPrice,
            previousPrice = previousPrice,
            direction = direction.toUi(),
            description = description
        )

    fun ConnectionStateEntity.toConnectionStateUi(): ConnectionStateUi = when (this) {
        is ConnectionStateEntity.Connected -> ConnectionStateUi.Connected
        is ConnectionStateEntity.Disconnected -> ConnectionStateUi.Disconnected
        is ConnectionStateEntity.Connecting -> ConnectionStateUi.Connecting
    }

    private fun PriceDirectionEntity.toUi(): PriceDirectionUi = when (this) {
        PriceDirectionEntity.UP -> PriceDirectionUi.UP
        PriceDirectionEntity.DOWN -> PriceDirectionUi.DOWN
        PriceDirectionEntity.NEUTRAL -> PriceDirectionUi.NEUTRAL
    }
}
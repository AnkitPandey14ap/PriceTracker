package com.multibank.pricetracker.ui.feature.feed

import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [FeedMapper] (domain entity to UI model mapping).
 */
class FeedMapperTest {

    @Test
    fun stockSymbolEntity_toFeedItemUi_mapsAllFields() {
        val entity = StockSymbolEntity(
            symbol = "AAPL",
            currentPrice = 150.50,
            previousPrice = 148.25,
            direction = PriceDirectionEntity.UP,
            description = "Apple Inc."
        )

        val result = with(FeedMapper) { entity.toFeedItemUi() }

        assertEquals("AAPL", result.symbol)
        assertEquals(150.50, result.currentPrice, 0.0)
        assertEquals(148.25, result.previousPrice, 0.0)
        assertEquals(PriceDirectionUi.UP, result.direction)
        assertEquals("Apple Inc.", result.description)
        assertEquals(2.25, result.priceChange, 0.0)
    }

    @Test
    fun connectionStateEntity_Connected_mapsToConnectionStateUi() {
        val result = with(FeedMapper) { ConnectionStateEntity.Connected.toConnectionStateUi() }
        assertEquals(ConnectionStateUi.Connected, result)
    }

    @Test
    fun connectionStateEntity_Disconnected_mapsToConnectionStateUi() {
        val result = with(FeedMapper) { ConnectionStateEntity.Disconnected.toConnectionStateUi() }
        assertEquals(ConnectionStateUi.Disconnected, result)
    }

    @Test
    fun connectionStateEntity_Connecting_mapsToConnectionStateUi() {
        val result = with(FeedMapper) { ConnectionStateEntity.Connecting.toConnectionStateUi() }
        assertEquals(ConnectionStateUi.Connecting, result)
    }
}

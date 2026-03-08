package com.multibank.pricetracker.data.mapper

import com.multibank.pricetracker.data.dto.PriceDirectionDto
import com.multibank.pricetracker.data.dto.StockSymbolDto
import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class StockSymbolMapperTest {

    @Test
    fun toDomain_mapsDtoToEntity() {
        val dto = StockSymbolDto(
            symbol = "NVDA",
            currentPrice = 500.50,
            previousPrice = 498.25,
            direction = PriceDirectionDto.UP,
            description = "NVIDIA"
        )

        val result = StockSymbolMapper.toDomain(dto)

        assertEquals("NVDA", result.symbol)
        assertEquals(500.50, result.currentPrice, 0.0)
        assertEquals(498.25, result.previousPrice, 0.0)
        assertEquals(PriceDirectionEntity.UP, result.direction)
        assertEquals("NVIDIA", result.description)
    }

    @Test
    fun toDto_mapsEntityToDto() {
        val entity = StockSymbolEntity(
            symbol = "GOOG",
            currentPrice = 140.0,
            previousPrice = 138.0,
            direction = PriceDirectionEntity.DOWN,
            description = "Alphabet"
        )

        val result = StockSymbolMapper.toDto(entity)

        assertEquals(StockSymbolDto("GOOG", 140.0, 138.0, PriceDirectionDto.DOWN, "Alphabet"), result)
    }

    @Test
    fun toDomain_thenToDto_roundTrips() {
        val dto = StockSymbolDto("TSLA", 250.0, 248.0, PriceDirectionDto.NEUTRAL, "Tesla")

        val entity = StockSymbolMapper.toDomain(dto)
        val back = StockSymbolMapper.toDto(entity)

        assertEquals(dto, back)
    }
}

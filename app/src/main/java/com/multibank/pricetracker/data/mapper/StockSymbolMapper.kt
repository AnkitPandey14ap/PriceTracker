package com.multibank.pricetracker.data.mapper

import com.multibank.pricetracker.data.PriceDirectionDto
import com.multibank.pricetracker.data.StockSymbolDto
import com.multibank.pricetracker.domain.model.PriceDirection
import com.multibank.pricetracker.domain.model.StockSymbol

object StockSymbolMapper {

    fun toDomain(dto: StockSymbolDto): StockSymbol =
        StockSymbol(
            symbol = dto.symbol,
            currentPrice = dto.currentPrice,
            previousPrice = dto.previousPrice,
            direction = dto.direction.toDomain(),
            description = dto.description
        )

    fun toDto(domain: StockSymbol): StockSymbolDto =
        StockSymbolDto(
            symbol = domain.symbol,
            currentPrice = domain.currentPrice,
            previousPrice = domain.previousPrice,
            direction = domain.direction.toDto(),
            description = domain.description
        )

    private fun PriceDirectionDto.toDomain(): PriceDirection = when (this) {
        PriceDirectionDto.UP -> PriceDirection.UP
        PriceDirectionDto.DOWN -> PriceDirection.DOWN
        PriceDirectionDto.NEUTRAL -> PriceDirection.NEUTRAL
    }

    private fun PriceDirection.toDto(): PriceDirectionDto = when (this) {
        PriceDirection.UP -> PriceDirectionDto.UP
        PriceDirection.DOWN -> PriceDirectionDto.DOWN
        PriceDirection.NEUTRAL -> PriceDirectionDto.NEUTRAL
    }
}

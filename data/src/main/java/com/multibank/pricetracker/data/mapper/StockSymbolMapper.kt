package com.multibank.pricetracker.data.mapper

import com.multibank.pricetracker.data.dto.PriceDirectionDto
import com.multibank.pricetracker.data.dto.StockSymbolDto
import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity

object StockSymbolMapper {

    fun toDomain(dto: StockSymbolDto): StockSymbolEntity =
        StockSymbolEntity(
            symbol = dto.symbol,
            currentPrice = dto.currentPrice,
            previousPrice = dto.previousPrice,
            direction = dto.direction.toDomain(),
            description = dto.description
        )

    fun toDto(domain: StockSymbolEntity): StockSymbolDto =
        StockSymbolDto(
            symbol = domain.symbol,
            currentPrice = domain.currentPrice,
            previousPrice = domain.previousPrice,
            direction = domain.direction.toDto(),
            description = domain.description
        )

    private fun PriceDirectionDto.toDomain(): PriceDirectionEntity = when (this) {
        PriceDirectionDto.UP -> PriceDirectionEntity.UP
        PriceDirectionDto.DOWN -> PriceDirectionEntity.DOWN
        PriceDirectionDto.NEUTRAL -> PriceDirectionEntity.NEUTRAL
    }

    private fun PriceDirectionEntity.toDto(): PriceDirectionDto = when (this) {
        PriceDirectionEntity.UP -> PriceDirectionDto.UP
        PriceDirectionEntity.DOWN -> PriceDirectionDto.DOWN
        PriceDirectionEntity.NEUTRAL -> PriceDirectionDto.NEUTRAL
    }
}

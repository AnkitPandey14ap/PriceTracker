package com.multibank.pricetracker.data.mapper

import com.multibank.pricetracker.data.PriceUpdateDto
import com.multibank.pricetracker.domain.model.PriceUpdate

object PriceUpdateMapper {

    fun toDomain(dto: PriceUpdateDto): PriceUpdate =
        PriceUpdate(symbol = dto.symbol, price = dto.price)
}

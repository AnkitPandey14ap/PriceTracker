package com.multibank.pricetracker.data.mapper

import com.multibank.pricetracker.data.dto.PriceUpdateDto
import com.multibank.pricetracker.domain.model.PriceUpdateEntity

object PriceUpdateMapper {

    fun toDomain(dto: PriceUpdateDto): PriceUpdateEntity =
        PriceUpdateEntity(symbol = dto.symbol, price = dto.price)
}

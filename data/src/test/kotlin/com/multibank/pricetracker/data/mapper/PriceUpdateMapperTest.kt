package com.multibank.pricetracker.data.mapper

import com.multibank.pricetracker.data.dto.PriceUpdateDto
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceUpdateMapperTest {

    @Test
    fun toDomain_mapsDtoToEntity() {
        val dto = PriceUpdateDto(symbol = "AAPL", price = 150.25)

        val result = PriceUpdateMapper.toDomain(dto)

        assertEquals(PriceUpdateEntity("AAPL", 150.25), result)
    }
}

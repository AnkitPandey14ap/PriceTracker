package com.multibank.pricetracker.data.datasource

import com.multibank.pricetracker.data.dto.PriceUpdateDto
fun interface PriceMessageParser {

    fun parse(message: String): PriceUpdateDto?
}

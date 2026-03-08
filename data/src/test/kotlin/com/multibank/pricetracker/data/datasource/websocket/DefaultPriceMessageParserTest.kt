package com.multibank.pricetracker.data.datasource.websocket

import com.multibank.pricetracker.data.dto.PriceUpdateDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultPriceMessageParserTest {

    private val parser = DefaultPriceMessageParser()

    @Test
    fun parse_validFormat_returnsDto() {
        val result = parser.parse("AAPL:150.25")

        assertEquals(PriceUpdateDto("AAPL", 150.25), result)
    }

    @Test
    fun parse_withSpaces_trimsAndReturnsDto() {
        val result = parser.parse("  GOOG : 140.5  ")

        assertEquals(PriceUpdateDto("GOOG", 140.5), result)
    }

    @Test
    fun parse_invalidPrice_returnsNull() {
        val result = parser.parse("NVDA:not-a-number")

        assertNull(result)
    }

    @Test
    fun parse_singlePart_returnsNull() {
        assertNull(parser.parse("AAPL"))
    }

    @Test
    fun parse_threeParts_returnsNull() {
        assertNull(parser.parse("A:B:C"))
    }

    @Test
    fun parse_emptyString_returnsNull() {
        assertNull(parser.parse(""))
    }
}

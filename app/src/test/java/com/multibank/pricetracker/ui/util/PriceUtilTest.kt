package com.multibank.pricetracker.ui.util

import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [PriceUtil].
 */
class PriceUtilTest {

    @Test
    fun priceDirection_newGreaterThanOld_returnsUp() {
        assertEquals(PriceDirectionEntity.UP, PriceUtil.priceDirection(100.0, 90.0))
    }

    @Test
    fun priceDirection_newLessThanOld_returnsDown() {
        assertEquals(PriceDirectionEntity.DOWN, PriceUtil.priceDirection(80.0, 90.0))
    }

    @Test
    fun priceDirection_newEqualsOld_returnsNeutral() {
        assertEquals(PriceDirectionEntity.NEUTRAL, PriceUtil.priceDirection(90.0, 90.0))
    }

    @Test
    fun priceChangePercent_positiveChange_returnsCorrectPercent() {
        val result = PriceUtil.priceChangePercent(110.0, 100.0)
        assertEquals(10.0, result, 0.001)
    }

    @Test
    fun priceChangePercent_negativeChange_returnsPositivePercent() {
        val result = PriceUtil.priceChangePercent(90.0, 100.0)
        assertEquals(10.0, result, 0.001)
    }

    @Test
    fun priceChangePercent_oldZero_returnsZero() {
        val result = PriceUtil.priceChangePercent(100.0, 0.0)
        assertEquals(0.0, result, 0.0)
    }
}

package com.multibank.pricetracker.ui.feed

import com.multibank.pricetracker.domain.model.PriceDirection

object PriceUtil {
    fun priceDirection(new: Double, old: Double): PriceDirection =
        when {
            new > old -> PriceDirection.UP
            new < old -> PriceDirection.DOWN
            else -> PriceDirection.NEUTRAL
        }


    fun priceChangePercent(new: Double, old: Double): Double {
        if (old <= 0) return 0.0
        return kotlin.math.abs((new - old) / old * 100)
    }

}
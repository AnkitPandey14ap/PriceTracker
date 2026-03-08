package com.multibank.pricetracker.ui.util

import com.multibank.pricetracker.domain.model.PriceDirectionEntity
import kotlin.math.abs

object PriceUtil {
    fun priceDirection(new: Double, old: Double): PriceDirectionEntity =
        when {
            new > old -> PriceDirectionEntity.UP
            new < old -> PriceDirectionEntity.DOWN
            else -> PriceDirectionEntity.NEUTRAL
        }


    fun priceChangePercent(new: Double, old: Double): Double {
        if (old <= 0) return 0.0
        return abs((new - old) / old * 100)
    }

}
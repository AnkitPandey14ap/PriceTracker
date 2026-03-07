package com.multibank.pricetracker.util

import kotlin.math.abs


fun formatPrice(price: Double): String =
    "$%.2f".format(price)

fun formatPriceChange(change: Double, percentChange: Double): String {
    val sign = if (change >= 0) "+" else "-"
    return "%s$%.2f (%s%.2f%%)".format(
        sign, abs(change), sign, abs(percentChange)
    )
}

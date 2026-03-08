package com.multibank.pricetracker.ui.navigation

sealed class Destinations(val route: String) {
    data object Feed : Destinations("feed")
    data object Detail : Destinations("detail/{symbol}") {
        fun createRoute(symbol: String) = "detail/$symbol"
    }
}
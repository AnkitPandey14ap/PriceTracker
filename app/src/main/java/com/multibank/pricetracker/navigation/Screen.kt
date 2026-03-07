package com.multibank.pricetracker.navigation

sealed class Screen(val route: String) {
    data object Feed : Screen("feed")
    data object Detail : Screen("detail/{symbol}") {
        fun createRoute(symbol: String) = "detail/$symbol"
    }
}
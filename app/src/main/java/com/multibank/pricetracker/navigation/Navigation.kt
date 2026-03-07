package com.multibank.pricetracker.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.multibank.pricetracker.ui.detail.SymbolDetailScreen
import com.multibank.pricetracker.ui.feed.FeedScreen



@Composable
fun PriceTrackerNavHost(intent: Intent? = null) {
    val navController = rememberNavController()

    // Handle deep link: stocks://symbol/{symbol}
    val deepLinkSymbol = intent?.data?.let { uri ->
        if (uri.scheme == "stocks" && uri.host == "symbol") {
            uri.lastPathSegment
        } else null
    }

    val startDestination = if (deepLinkSymbol != null) {
        Screen.Detail.createRoute(deepLinkSymbol)
    } else {
        Screen.Feed.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Feed.route) {
            FeedScreen(
                onSymbolClick = { symbol ->
                    navController.navigate(Screen.Detail.createRoute(symbol))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("symbol") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "stocks://symbol/{symbol}" }
            )
        ) {
            SymbolDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

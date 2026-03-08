package com.multibank.pricetracker.ui.feature.feed

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedUiState
import com.multibank.pricetracker.ui.theme.MultibankPriceTrackerTheme
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for [FeedScreen] using [FakeFeedViewModel].
 */
class FeedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun feedScreen_showsTitleAndStocks() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(
                stocks = listOf(
                    FeedItemUi("AAPL", 150.0, 148.0, PriceDirectionUi.UP, "Apple"),
                    FeedItemUi("GOOG", 140.0, 138.0, PriceDirectionUi.DOWN, "Alphabet")
                ),
                connectionState = ConnectionStateUi.Connected,
                isFeedRunning = false
            )
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithText("Price Tracker").assertExists()
        composeTestRule.onNodeWithText("AAPL").assertExists()
        composeTestRule.onNodeWithText("GOOG").assertExists()
    }

    @Test
    fun feedScreen_toggleButton_showsStartWhenNotRunning() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(stocks = emptyList(), connectionState = ConnectionStateUi.Disconnected, isFeedRunning = false)
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithText("▶ Start").assertExists()
    }

    @Test
    fun feedScreen_toggleButton_showsStopWhenRunning() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(stocks = emptyList(), connectionState = ConnectionStateUi.Connected, isFeedRunning = true)
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithText("⏹ Stop").assertExists()
    }

    @Test
    fun feedScreen_clickToggle_sendsToggleIntent() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(stocks = emptyList(), connectionState = ConnectionStateUi.Disconnected, isFeedRunning = false)
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("feed_toggle_button").performClick()

        assert(fakeVm.recordedIntents.size == 1)
        assert(fakeVm.recordedIntents[0] is com.multibank.pricetracker.ui.feature.feed.mvi.FeedIntent.ToggleConnection)
    }

    @Test
    fun feedScreen_clickStockRow_invokesOnSymbolClick() {
        var clickedSymbol: String? = null
        val fakeVm = FakeFeedViewModel(
            FeedUiState(
                stocks = listOf(FeedItemUi("NVDA", 500.0, 498.0, PriceDirectionUi.UP, "NVIDIA")),
                connectionState = ConnectionStateUi.Connected,
                isFeedRunning = false
            )
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = { clickedSymbol = it }, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithText("NVDA").performClick()

        assert(clickedSymbol == "NVDA")
    }

    @Test
    fun feedScreen_emptyList_showsNoStockRows() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(stocks = emptyList(), connectionState = ConnectionStateUi.Disconnected, isFeedRunning = false)
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithText("Price Tracker").assertExists()
        composeTestRule.onNodeWithTag("feed_toggle_button").assertExists()
        composeTestRule.onNodeWithTag("feed_stock_row_AAPL").assertDoesNotExist()
        composeTestRule.onNodeWithTag("feed_stock_row_GOOG").assertDoesNotExist()
    }

    @Test
    fun feedScreen_stockRow_showsPriceAndChange() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(
                stocks = listOf(
                    FeedItemUi("MSFT", 400.50, 398.0, PriceDirectionUi.UP, "Microsoft")
                ),
                connectionState = ConnectionStateUi.Connected,
                isFeedRunning = false
            )
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("feed_stock_row_MSFT").assertExists()
        composeTestRule.onNodeWithText("$400.50").assertExists()
        composeTestRule.onNodeWithText("+$2.50 (+0.63%)").assertExists()
    }

    @Test
    fun feedScreen_connectionDot_exists() {
        val fakeVm = FakeFeedViewModel(
            FeedUiState(stocks = emptyList(), connectionState = ConnectionStateUi.Connecting, isFeedRunning = true)
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                FeedScreen(onSymbolClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("feed_connection_dot").assertExists()
    }
}

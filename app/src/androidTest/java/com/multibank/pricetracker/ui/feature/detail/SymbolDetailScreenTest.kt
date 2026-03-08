package com.multibank.pricetracker.ui.feature.detail

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailUiState
import com.multibank.pricetracker.ui.theme.MultibankPriceTrackerTheme
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for [SymbolDetailScreen] using [FakeDetailViewModel].
 */
class SymbolDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreen_showsSymbolTitleAndPrice() {
        val stock = FeedItemUi(
            "TSLA",
            250.75,
            248.0,
            PriceDirectionUi.UP,
            "Tesla — electric vehicles and energy."
        )
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = stock, connectionState = ConnectionStateUi.Connected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_symbol_title").assertExists()
        composeTestRule.onNodeWithText("About TSLA").assertExists()
        composeTestRule.onNodeWithText("Tesla — electric vehicles and energy.").assertExists()
    }

    @Test
    fun detailScreen_clickBack_sendsNavigateBackIntent() {
        val fakeVm = FakeDetailViewModel(
            DetailUiState(
                stock = FeedItemUi("AAPL", 150.0, 148.0, PriceDirectionUi.NEUTRAL, "Apple"),
                connectionState = ConnectionStateUi.Connected
            )
        )

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_back_button").performClick()

        assert(fakeVm.recordedIntents.size == 1)
        assert(fakeVm.recordedIntents[0] is com.multibank.pricetracker.ui.feature.detail.mvi.DetailIntent.NavigateBack)
    }

    @Test
    fun detailScreen_showsPriceCardAndFormattedPrice() {
        val stock = FeedItemUi(
            "AMZN",
            185.25,
            183.0,
            PriceDirectionUi.DOWN,
            "Amazon — e-commerce and cloud."
        )
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = stock, connectionState = ConnectionStateUi.Connected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_price_card").assertExists()
        composeTestRule.onNodeWithTag("detail_price_value").assertExists()
        composeTestRule.onNodeWithTag("detail_change_value").assertExists()
    }

    @Test
    fun detailScreen_showsConnectionStateLive() {
        val stock = FeedItemUi("META", 500.0, 498.0, PriceDirectionUi.UP, "Meta Platforms.")
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = stock, connectionState = ConnectionStateUi.Connected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_connection_state").assertExists()
        composeTestRule.onNodeWithText("Live").assertExists()
    }

    @Test
    fun detailScreen_showsConnectionStateDisconnected() {
        val stock = FeedItemUi("META", 500.0, 498.0, PriceDirectionUi.UP, "Meta Platforms.")
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = stock, connectionState = ConnectionStateUi.Disconnected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithText("Disconnected").assertExists()
    }

    @Test
    fun detailScreen_showsMarketStats() {
        val stock = FeedItemUi(
            "NVDA",
            900.0,
            880.0,
            PriceDirectionUi.UP,
            "NVIDIA Corporation."
        )
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = stock, connectionState = ConnectionStateUi.Connected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_market_stats").assertExists()
        composeTestRule.onNodeWithText("Market Stats").assertExists()
        composeTestRule.onNodeWithText("Symbol").assertExists()
        composeTestRule.onNodeWithText("Current Price").assertExists()
        composeTestRule.onNodeWithTag("detail_stats_current_price").assertExists()
    }

    @Test
    fun detailScreen_showsFooter() {
        val stock = FeedItemUi("GOOG", 140.0, 138.0, PriceDirectionUi.NEUTRAL, "Alphabet.")
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = stock, connectionState = ConnectionStateUi.Connected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_footer").assertExists()
        composeTestRule.onNodeWithText("Prices update every 2 seconds via WebSocket echo").assertExists()
    }

    @Test
    fun detailScreen_nullStock_showsEmptyContent() {
        val fakeVm = FakeDetailViewModel(DetailUiState(stock = null, connectionState = ConnectionStateUi.Disconnected))

        composeTestRule.setContent {
            MultibankPriceTrackerTheme {
                SymbolDetailScreen(onBackClick = {}, viewModel = fakeVm)
            }
        }

        composeTestRule.onNodeWithTag("detail_back_button").assertExists()
        composeTestRule.onNodeWithTag("detail_symbol_title").assertExists()
        composeTestRule.onNodeWithTag("detail_price_card").assertDoesNotExist()
        composeTestRule.onNodeWithTag("detail_market_stats").assertDoesNotExist()
    }
}

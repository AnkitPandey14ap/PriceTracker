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

}

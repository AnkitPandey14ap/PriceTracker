package com.multibank.pricetracker.ui.feature.feed

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multibank.pricetracker.ui.common.ToastHelper
import com.multibank.pricetracker.ui.feature.feed.components.FeedStockList
import com.multibank.pricetracker.ui.feature.feed.components.FeedTopBar
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedIntent
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onSymbolClick: (String) -> Unit,
    viewModel: FeedViewModelContract = hiltViewModel<FeedViewModel>()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.feedSideEffect.collect {
            when (it) {
                is FeedSideEffect.NavigateToDetailPage -> onSymbolClick(it.id)
                is FeedSideEffect.ShowToast -> ToastHelper.show(context, it.text)
            }
        }
    }

    Scaffold(
        topBar = {
            FeedTopBar(
                connectionState = uiState.connectionState,
                isFeedRunning = uiState.isFeedRunning,
                onToggleClick = { viewModel.sendIntent(FeedIntent.ToggleConnection) }
            )
        }
    ) { innerPadding ->
        FeedStockList(
            stocks = uiState.stocks,
            flashMap = uiState.flashMap,
            onSymbolClick = onSymbolClick,
            contentPadding = innerPadding
        )
    }
}

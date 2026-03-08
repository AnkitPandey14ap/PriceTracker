package com.multibank.pricetracker.ui.feature.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multibank.pricetracker.ui.feature.detail.components.DetailContent
import com.multibank.pricetracker.ui.feature.detail.components.DetailTopBar
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailIntent
import com.multibank.pricetracker.ui.feature.detail.mvi.DetailSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolDetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModelContract = hiltViewModel<DetailViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stock = uiState.stock

    LaunchedEffect(Unit) {
        viewModel.detailSideEffect.collect {
            when (it) {
                DetailSideEffect.NavigateBack -> onBackClick()
            }
        }
    }

    val defaultBg = MaterialTheme.colorScheme.background
    val flashGreen = Color(0xFF4CAF50).copy(alpha = 0.10f)
    val flashRed = Color(0xFFF44336).copy(alpha = 0.10f)
    val targetBg = when (uiState.flash) {
        true -> flashGreen
        false -> flashRed
        null -> defaultBg
    }
    val animatedBg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(durationMillis = 600, easing = EaseInOut),
        label = "detailFlash"
    )

    Scaffold(
        topBar = {
            DetailTopBar(
                symbol = stock?.symbol ?: "",
                onBackClick = { viewModel.sendIntent(DetailIntent.NavigateBack) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBg)
                .padding(innerPadding)
        ) {
            if (stock != null) {
                DetailContent(stock = stock, connectionState = uiState.connectionState)
            }
        }
    }
}

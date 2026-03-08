package com.multibank.pricetracker.ui.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi

/**
 * Small status dot indicating connection state (connected / disconnected / connecting).
 */
@Composable
fun ConnectionDot(
    state: ConnectionStateUi,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        is ConnectionStateUi.Connected -> Color(0xFF4CAF50)
        is ConnectionStateUi.Disconnected -> Color(0xFFF44336)
        is ConnectionStateUi.Connecting -> Color(0xFFFF9800)
    }
    Box(
        modifier = modifier
            .size(11.dp)
            .clip(CircleShape)
            .background(color)
    )
}

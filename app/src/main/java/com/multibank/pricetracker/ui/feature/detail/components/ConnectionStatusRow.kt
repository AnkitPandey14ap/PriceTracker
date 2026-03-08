package com.multibank.pricetracker.ui.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multibank.pricetracker.R
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi

/**
 * Row showing a small status dot and label (Live / Disconnected / Connecting…).
 */
@Composable
fun ConnectionStatusRow(
    connectionState: ConnectionStateUi,
    modifier: Modifier = Modifier
) {
    val dotColor = when (connectionState) {
        is ConnectionStateUi.Connected -> Color(0xFF4CAF50)
        is ConnectionStateUi.Disconnected -> Color(0xFFF44336)
        is ConnectionStateUi.Connecting -> Color(0xFFFF9800)
    }
    val stateLabel = when (connectionState) {
        is ConnectionStateUi.Connected -> stringResource(R.string.connection_live)
        is ConnectionStateUi.Disconnected -> stringResource(R.string.connection_disconnected)
        is ConnectionStateUi.Connecting -> stringResource(R.string.connection_connecting)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics { testTag = "detail_connection_state" }
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stateLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

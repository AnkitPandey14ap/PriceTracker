package com.multibank.pricetracker.ui.feature.detail.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

/**
 * Footer text explaining WebSocket update frequency.
 */
@Composable
fun DetailFooter(modifier: Modifier = Modifier) {
    Text(
        text = "Prices update every 2 seconds via WebSocket echo",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.semantics { testTag = "detail_footer" }
    )
}

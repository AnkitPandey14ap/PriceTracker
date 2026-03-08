

package com.multibank.pricetracker.ui.feature.feed.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi

/**
 * Top app bar for the feed: title with connection dot and toggle button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    connectionState: ConnectionStateUi,
    isFeedRunning: Boolean,
    onToggleClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConnectionDot(
                    state = connectionState,
                    modifier = Modifier.semantics { testTag = "feed_connection_dot" }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Price Tracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            ToggleFeedButton(
                isRunning = isFeedRunning,
                onClick = onToggleClick,
                modifier = Modifier.semantics { testTag = "feed_toggle_button" }
            )
            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

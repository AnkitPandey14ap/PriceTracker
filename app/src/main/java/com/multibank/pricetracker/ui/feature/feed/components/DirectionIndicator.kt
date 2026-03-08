package com.multibank.pricetracker.ui.feature.feed.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi

/** Arrow character for a price direction. */
fun PriceDirectionUi.toArrow(): String = when (this) {
    PriceDirectionUi.UP -> "↑"
    PriceDirectionUi.DOWN -> "↓"
    PriceDirectionUi.NEUTRAL -> "—"
}

/** Theme-aware color for a price direction. */
@Composable
fun directionColor(direction: PriceDirectionUi): Color = when (direction) {
    PriceDirectionUi.UP -> Color(0xFF4CAF50)
    PriceDirectionUi.DOWN -> Color(0xFFF44336)
    PriceDirectionUi.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
}

/**
 * Small arrow text showing price direction (↑ / ↓ / —) with theme-aware color.
 */
@Composable
fun DirectionArrow(
    direction: PriceDirectionUi,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp
) {
    Text(
        text = direction.toArrow(),
        color = directionColor(direction),
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        modifier = modifier
    )
}

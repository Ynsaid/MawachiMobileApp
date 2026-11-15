package com.example.mawachiapp.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mawachiapp.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal val MarketGreen = Color(0xFF344C36)
internal val MarketBackground = Color(0xFFF6F7F1)
internal val MarketLightGreen = Color(0xFFE8EEDF)
internal val MarketText = Color(0xFF243126)
internal val MarketMuted = Color(0xFF748071)
internal val MarketGold = Color(0xFFF1D390)
internal val MarketRed = Color(0xFFB54E46)

internal val MarketFont = FontFamily(
    Font(R.font.changa_medium, FontWeight.Normal),
    Font(R.font.changa_bold, FontWeight.Bold)
)

internal fun formatMoney(value: BigDecimal): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 2
    }

    return "${formatter.format(value)} دج"
}

@Composable
internal fun MarketImage(
    url: String?,
    description: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MarketLightGreen),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = null,
            tint = MarketMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(32.dp)
        )

        AsyncImage(
            model = url,
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
internal fun MarketEmptyState(
    icon: ImageVector,
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MarketLightGreen
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MarketGreen,
                modifier = Modifier
                    .padding(24.dp)
                    .size(38.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = message,
            color = MarketMuted,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}
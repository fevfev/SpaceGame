package com.students.spacegame.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameHUD(
    score: Int,
    health: Int,
    level: Int,
    credits: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🎯 Счет: $score",
                color = Color.Yellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "❤️ Здоровье: $health",
                color = Color.Red,
                fontSize = 14.sp
            )
            Text(
                text = "🌌 Зона: $level",
                color = Color.Cyan,
                fontSize = 14.sp
            )
            Text(
                text = "💰 Кредиты: $credits",
                color = Color.Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

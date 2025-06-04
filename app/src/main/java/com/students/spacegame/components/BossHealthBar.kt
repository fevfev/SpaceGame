package com.students.spacegame.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.students.spacegame.models.Boss

@Composable
fun BossHealthBar(
    boss: Boss,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = boss.name,
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
        ) {
            val healthPercentage = boss.health.toFloat() / boss.maxHealth.toFloat()

            // Фон полоски здоровья
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(10.dp.toPx())
            )

            // Полоска здоровья
            drawRoundRect(
                color = when {
                    healthPercentage > 0.6f -> Color.Green
                    healthPercentage > 0.3f -> Color.Yellow
                    else -> Color.Red
                },
                topLeft = Offset.Zero,
                size = Size(size.width * healthPercentage, size.height),
                cornerRadius = CornerRadius(10.dp.toPx())
            )
        }

        Text(
            text = "${boss.health} / ${boss.maxHealth}",
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

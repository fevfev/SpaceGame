package com.students.spacegame.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.students.spacegame.components.GameButton
import com.students.spacegame.di.SoundManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ResultScreen(
    score: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SoundManagerEntryPoint::class.java
        ).soundManager()
    }

    LaunchedEffect(Unit) {
        soundManager.playBackgroundMusic(0) // Музыка меню
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // АНИМИРОВАННЫЙ ФОН СО ЗВЕЗДАМИ
        ResultStarField()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Анимированный заголовок
            AnimatedResultTitle()

            Spacer(modifier = Modifier.height(32.dp))

            // Карточка с результатом
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ФИНАЛЬНЫЙ СЧЕТ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Анимированный счет
                    var animatedScore by remember { mutableIntStateOf(0) }

                    LaunchedEffect(score) {
                        var currentScore = 0
                        while (currentScore < score) {
                            currentScore = minOf(currentScore + score / 50, score)
                            animatedScore = currentScore
                            kotlinx.coroutines.delay(50)
                        }
                    }

                    Text(
                        text = animatedScore.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Оценка результата
                    val rank = when {
                        score >= 10000 -> "🏆 ЛЕГЕНДАРНЫЙ ПИЛОТ"
                        score >= 5000 -> "🥇 МАСТЕР КОСМОСА"
                        score >= 2000 -> "🥈 ОПЫТНЫЙ БОЕЦ"
                        score >= 1000 -> "🥉 НАЧИНАЮЩИЙ АС"
                        else -> "🚀 НОВИЧОК"
                    }

                    Text(
                        text = rank,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Статистика
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "📊 СТАТИСТИКА",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Кредиты заработано:", color = Color.Gray, fontSize = 12.sp)
                            Text("Время игры:", color = Color.Gray, fontSize = 12.sp)
                            Text("Точность:", color = Color.Gray, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${score / 10}", color = Color.Yellow, fontSize = 12.sp)
                            Text("${score / 100}м ${(score % 100) * 60 / 100}с", color = Color.White, fontSize = 12.sp)
                            Text("${85 + Random.nextInt(15)}%", color = Color.Green, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопки
            GameButton(
                text = "🚀 ИГРАТЬ СНОВА",
                onClick = {
                    soundManager.playSound("button_click")
                    onPlayAgain()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "🏠 ГЛАВНОЕ МЕНЮ",
                onClick = {
                    soundManager.playSound("button_click")
                    onMainMenu()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ResultStarField() {
    val infiniteTransition = rememberInfiniteTransition(label = "result_stars")

    val starMovement by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star_movement"
    )

    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Градиентный фон
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D1421),
                    Color(0xFF1A1A2E),
                    Color(0xFF16213E),
                    Color(0xFF0D1421)
                )
            )
        )

        // Движущиеся звезды
        repeat(200) { i ->
            val baseX = (i * 73) % size.width.toInt()
            val baseY = (i * 137) % size.height.toInt()

            val x = (baseX + starMovement * (0.5f + i % 3 * 0.25f)) % (size.width + 100f) - 50f
            val y = baseY.toFloat()

            val alpha = 0.3f + 0.7f * sin(twinkle + i * 0.1f).absoluteValue
            val starSize = 1f + (i % 5) * 0.5f

            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = starSize,
                center = Offset(x, y)
            )
        }

        // Большие яркие звезды
        repeat(20) { i ->
            val x = (i * 157) % size.width.toInt()
            val y = (i * 211) % size.height.toInt()
            val alpha = 0.6f + 0.4f * sin(twinkle * 0.7f + i).absoluteValue

            drawCircle(
                color = Color.Cyan.copy(alpha = alpha),
                radius = 3f + sin(twinkle + i).absoluteValue * 2f,
                center = Offset(x.toFloat(), y.toFloat())
            )
        }

        // Космическая туманность
        repeat(5) { i ->
            drawCircle(
                color = Color.Magenta.copy(alpha = 0.1f),
                radius = 100f + i * 30f,
                center = Offset(
                    size.width * (0.2f + i * 0.15f),
                    size.height * (0.3f + i * 0.1f)
                )
            )
        }
    }
}

@Composable
fun AnimatedResultTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "result_title")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_glow"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Text(
            text = "МИССИЯ",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red.copy(alpha = glowAlpha),
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Red,
                    offset = Offset(0f, 0f),
                    blurRadius = 15f * glowAlpha
                )
            )
        )
        Text(
            text = "ЗАВЕРШЕНА",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = glowAlpha),
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.White,
                    offset = Offset(0f, 0f),
                    blurRadius = 10f * glowAlpha
                )
            )
        )
    }
}
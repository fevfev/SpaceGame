package com.students.spacegame.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.max
import com.students.spacegame.components.GameButton
import com.students.spacegame.di.SoundManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.random.Random

/**
 * Экран главного меню игры Space Warriors.
 * Использует Jetpack Compose: https://developer.android.com/jetpack/compose
 * Здесь реализованы анимированный фон, заголовок и кнопки меню.
 */
@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onUpgradesClick: () -> Unit,
    onAchievementsClick: () -> Unit
) {
    // Получаем SoundManager через Hilt (Dependency Injection)
    // Подробнее: https://developer.android.com/training/dependency-injection/hilt-android
    val context = LocalContext.current
    val soundManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SoundManagerEntryPoint::class.java
        ).soundManager()
    }

    // Эффект для запуска фоновой музыки меню
    // Подробнее: https://developer.android.com/jetpack/compose/side-effects
    LaunchedEffect(Unit) {
        soundManager.playBackgroundMusic(0) // Музыка меню
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMenuBackground() // Анимированный фон (см. ниже)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Анимированный заголовок (см. функцию AnimatedTitle ниже)
            AnimatedTitle()

            Spacer(modifier = Modifier.height(64.dp))

            // Кнопки меню с анимацией появления
            AnimatedMenuButton(
                text = "🚀 НОВАЯ ИГРА",
                onClick = {
                    soundManager.playSound("button_click")
                    onPlayClick()
                },
                modifier = Modifier.fillMaxWidth(),
                delay = 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedMenuButton(
                text = "⚡ АПГРЕЙДЫ",
                onClick = {
                    soundManager.playSound("button_click")
                    onUpgradesClick()
                },
                modifier = Modifier.fillMaxWidth(),
                delay = 200
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedMenuButton(
                text = "🏆 РЕКОРДЫ",
                onClick = {
                    soundManager.playSound("button_click")
                    onLeaderboardClick()
                },
                modifier = Modifier.fillMaxWidth(),
                delay = 400
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedMenuButton(
                text = "🏅 ДОСТИЖЕНИЯ",
                onClick = {
                    soundManager.playSound("button_click")
                    onAchievementsClick()
                },
                modifier = Modifier.fillMaxWidth(),
                delay = 600
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedMenuButton(
                text = "⚙️ НАСТРОЙКИ",
                onClick = {
                    soundManager.playSound("button_click")
                    onSettingsClick()
                },
                modifier = Modifier.fillMaxWidth(),
                delay = 800
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Информация о версии
            Text(
                text = "v1.0.0 | Space Warriors\nРазработано для изучения Android",
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Анимированный фон для главного меню.
 * Использует Canvas и анимированные элементы Compose.
 * Документация: https://developer.android.com/jetpack/compose/graphics/draw
 */
@Composable
fun AnimatedMenuBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "menu_bg")

    // Анимация для планет
    val planet1X by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet1"
    )

    val planet2X by infiniteTransition.animateFloat(
        initialValue = 1200f,
        targetValue = -300f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet2"
    )

    val planet3X by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 1300f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet3"
    )

    // Анимация звезд
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stars"
    )

    // Анимация кораблей
    val ship1Y by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ship1Y"
    )
    val ship2Y by infiniteTransition.animateFloat(
        initialValue = 700f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ship2Y"
    )
    val ship3Y by infiniteTransition.animateFloat(
        initialValue = 400f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ship3Y"
    )
    val ship1X by infiniteTransition.animateFloat(
        initialValue = -120f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ship1X"
    )
    val ship2X by infiniteTransition.animateFloat(
        initialValue = 1300f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ship2X"
    )
    val ship3X by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ship3X"
    )
    val bulletOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bulletOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Градиентный фон
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1421),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )

            // Анимированные звезды
            repeat(150) { i ->
                val x = (i * 123) % size.width.toInt()
                val y = (i * 456) % size.height.toInt()
                val alpha = 0.3f + 0.7f * sin(starTwinkle + i * 0.1f).absoluteValue
                val starSize = 1f + (i % 4) * 0.5f

                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = starSize,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }

            // Анимированные планеты
            drawPlanet(planet1X, size.height * 0.2f, 80f, Color.Blue)
            drawPlanet(planet2X, size.height * 0.7f, 120f, Color.Red)
            drawPlanet(planet3X, size.height * 0.5f, 60f, Color.Green)

            // Астероиды
            repeat(25) { i ->
                val x = (planet1X * 0.3f + i * 50f) % (size.width + 200f) - 100f
                val y = (i * 47f) % size.height
                val asteroidSize = 3f + i % 8f

                drawCircle(
                    color = Color.Gray.copy(alpha = 0.4f),
                    radius = asteroidSize,
                    center = Offset(x, y)
                )
            }

            // Космическая пыль
            repeat(50) { i ->
                val x = (starTwinkle * 10f + i * 30f) % (size.width + 100f) - 50f
                val y = (i * 23f) % size.height

                drawCircle(
                    color = Color.Cyan.copy(alpha = 0.2f),
                    radius = 1f,
                    center = Offset(x, y)
                )
            }
        }

        // Летающие корабли поверх Canvas
        ShipWithBullets(
            painterRes = com.students.spacegame.R.drawable.ship_fighter,
            bulletRes = com.students.spacegame.R.drawable.bullet_laser,
            shipX = ship1X.dp,
            shipY = ship1Y.dp,
            bulletOffset = bulletOffset,
            angle = 0f,
            shipSize = 64.dp,
            bulletCount = 3
        )
        ShipWithBullets(
            painterRes = com.students.spacegame.R.drawable.ship_gunship,
            bulletRes = com.students.spacegame.R.drawable.bullet_plasma,
            shipX = ship2X.dp,
            shipY = ship2Y.dp,
            bulletOffset = bulletOffset,
            angle = 10f,
            shipSize = 72.dp,
            bulletCount = 2
        )
        ShipWithBullets(
            painterRes = com.students.spacegame.R.drawable.ship_sniper,
            bulletRes = com.students.spacegame.R.drawable.bullet_railgun,
            shipX = ship3X.dp,
            shipY = ship3Y.dp,
            bulletOffset = bulletOffset,
            angle = -8f,
            shipSize = 56.dp,
            bulletCount = 1
        )
    }
}

/**
 * Вспомогательная функция для отрисовки планеты с градиентом и атмосферой.
 * Используется в AnimatedMenuBackground.
 */
private fun DrawScope.drawPlanet(x: Float, y: Float, radius: Float, color: Color) {
    // Планета с градиентом
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.9f),
                color.copy(alpha = 0.6f),
                color.copy(alpha = 0.3f)
            ),
            center = Offset(x - radius * 0.3f, y - radius * 0.3f),
            radius = radius
        ),
        radius = radius,
        center = Offset(x, y)
    )

    // Атмосфера
    drawCircle(
        color = color.copy(alpha = 0.2f),
        radius = radius * 1.2f,
        center = Offset(x, y)
    )

    // Кольца для больших планет
    if (radius > 100f) {
        repeat(3) { ring ->
            val ringRadius = radius * (1.4f + ring * 0.2f)
            drawCircle(
                color = Color.White.copy(alpha = 0.1f - ring * 0.03f),
                radius = ringRadius,
                center = Offset(x, y),
                style = Stroke(width = 2f)
            )
        }
    }
}

/**
 * Вспомогательная функция для отрисовки корабля с выстрелами.
 * Используется в AnimatedMenuBackground.
 */
@Composable
fun ShipWithBullets(
    painterRes: Int,
    bulletRes: Int,
    shipX: Dp,
    shipY: Dp,
    bulletOffset: Float,
    angle: Float,
    shipSize: Dp,
    bulletCount: Int
) {
    Box(modifier = Modifier
        .padding(start = max(0.dp, shipX), top = max(0.dp, shipY))
        .size(shipSize)
    ) {
        Image(
            painter = painterResource(painterRes),
            contentDescription = null,
            modifier = Modifier
                .size(shipSize)
                .rotate(angle),
            contentScale = ContentScale.Fit
        )
        // Визуализация выстрелов
        for (i in 0 until bulletCount) {
            val bulletY = shipSize * (0.5f + 0.2f * (i - bulletCount / 2f))
            val bulletX = shipSize
            Image(
                painter = painterResource(bulletRes),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp, 12.dp)
                    .padding(
                        start = max(0.dp, bulletX * bulletOffset),
                        top = max(0.dp, bulletY)
                    )
                    .alpha(0.7f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Анимированный заголовок меню.
 * Использует эффекты свечения и пульсации.
 */
@Composable
fun AnimatedTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "title")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(pulseScale)
    ) {
        Text(
            text = "SPACE",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan.copy(alpha = glowAlpha),
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Cyan,
                    offset = Offset(0f, 0f),
                    blurRadius = 20f * glowAlpha
                )
            )
        )
        Text(
            text = "WARRIORS",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = glowAlpha),
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.White,
                    offset = Offset(0f, 0f),
                    blurRadius = 15f * glowAlpha
                )
            )
        )
    }
}

/**
 * Кнопка меню с анимацией появления.
 * Подробнее о анимациях: https://developer.android.com/jetpack/compose/animation
 */
@Composable
fun AnimatedMenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    delay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(800, easing = EaseOutBounce)
        ) + fadeIn(animationSpec = tween(800))
    ) {
        GameButton(
            text = text,
            onClick = onClick,
            modifier = modifier.height(56.dp)
        )
    }
}


package com.students.spacegame.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.students.spacegame.R
import com.students.spacegame.models.Zone
import kotlin.math.*
import kotlin.random.Random

@Composable
fun DynamicBackground(
    currentZone: Zone,
    modifier: Modifier = Modifier
) {
    LocalDensity.current

    // Анимации для звезд
    val infiniteTransition = rememberInfiniteTransition(label = "starfield")
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    // Анимация для планет
    val planetOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet_movement"
    )

    // Анимация для туманности
    val nebulaOpacity by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula_pulse"
    )

    // Генерируем стабильные позиции для звезд
    val stars = remember {
        List(200) { i ->
            AnimatedStar(
                x = Random(i).nextFloat(),
                y = Random(i * 2).nextFloat(),
                brightness = Random(i * 3).nextFloat(),
                size = Random(i * 4).nextFloat() * 2f + 0.5f,
                twinkleSpeed = Random(i * 5).nextFloat() * 0.5f + 0.5f
            )
        }
    }

    // Генерируем планеты для фона
    val backgroundPlanets = remember {
        List(3) { i ->
            BackgroundPlanet(
                initialX = Random(i * 10).nextFloat() * 1000f - 200f,
                y = Random(i * 11).nextFloat() * 800f + 100f,
                size = Random(i * 12).nextFloat() * 60f + 40f,
                speed = Random(i * 13).nextFloat() * 0.3f + 0.1f,
                color = when (currentZone.id) {
                    1 -> Color.Gray
                    2 -> Color.Blue
                    3 -> Color.Red
                    4 -> Color.Magenta
                    else -> Color.Cyan
                }.copy(alpha = 0.6f)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ОСНОВНОЙ ФОН по зонам
        Image(
            painter = painterResource(
                id = when (currentZone.id) {
                    1 -> R.drawable.bg_asteroid_belt
                    2 -> R.drawable.bg_nebula
                    3 -> R.drawable.bg_enemy_sector
                    4 -> R.drawable.bg_black_hole
                    5 -> R.drawable.bg_final_battle
                    else -> R.drawable.bg_space_default
                }
            ),
            contentDescription = "Zone Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // АНИМИРОВАННЫЕ ЭЛЕМЕНТЫ
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Звезды с анимацией
            drawAnimatedStars(stars, starTwinkle)

            // Планеты на фоне
            drawBackgroundPlanets(backgroundPlanets, planetOffset)

            // Специальные эффекты по зонам
            when (currentZone.id) {
                1 -> drawAsteroidField(planetOffset)
                2 -> drawNebula(nebulaOpacity)
                3 -> drawLaserGrid()
                4 -> drawBlackHoleEffect(planetOffset)
                5 -> drawBattleEffects()
            }
        }
    }
}

private fun DrawScope.drawAnimatedStars(stars: List<AnimatedStar>, twinkle: Float) {
    stars.forEach { star ->
        val finalAlpha = star.brightness * (0.7f + 0.3f * sin(twinkle * star.twinkleSpeed * PI.toFloat()))
        drawCircle(
            color = Color.White.copy(alpha = finalAlpha),
            radius = star.size,
            center = Offset(
                star.x * size.width,
                star.y * size.height
            )
        )
    }
}

private fun DrawScope.drawBackgroundPlanets(planets: List<BackgroundPlanet>, offset: Float) {
    planets.forEachIndexed { index, planet ->
        val x = (planet.initialX + offset * planet.speed) % (size.width + 400f) - 200f
        drawCircle(
            color = planet.color,
            radius = planet.size,
            center = Offset(x, planet.y)
        )
        // Атмосфера планеты
        drawCircle(
            color = planet.color.copy(alpha = 0.2f),
            radius = planet.size * 1.3f,
            center = Offset(x, planet.y)
        )
    }
}

private fun DrawScope.drawAsteroidField(offset: Float) {
    repeat(15) { i ->
        val x = (i * 80f + offset * 0.5f) % (size.width + 100f) - 50f
        val y = (i * 43f) % size.height
        drawCircle(
            color = Color.Gray.copy(alpha = 0.4f),
            radius = 5f + i % 8f,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawNebula(opacity: Float) {
    repeat(5) { i ->
        drawCircle(
            color = Color.Magenta.copy(alpha = opacity * 0.3f),
            radius = 150f + i * 50f,
            center = Offset(
                size.width * (0.2f + i * 0.2f),
                size.height * (0.3f + i * 0.1f)
            )
        )
    }
}

private fun DrawScope.drawLaserGrid() {
    val time = System.currentTimeMillis() / 50f
    repeat(8) { i ->
        val alpha = 0.3f * (0.5f + 0.5f * sin(time + i))
        drawLine(
            color = Color.Red.copy(alpha = alpha),
            start = Offset(0f, i * size.height / 8f),
            end = Offset(size.width, i * size.height / 8f),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawBlackHoleEffect(offset: Float) {
    val centerX = size.width * 0.8f
    val centerY = size.height * 0.2f

    repeat(10) { i ->
        val radius = 50f + i * 20f
        val alpha = 0.5f - i * 0.04f
        val rotationOffset = offset * 0.01f * (i + 1)

        drawCircle(
            color = Color.Black.copy(alpha = alpha),
            radius = radius,
            center = Offset(
                centerX + cos(rotationOffset) * 10f,
                centerY + sin(rotationOffset) * 10f
            )
        )
    }
}

private fun DrawScope.drawBattleEffects() {
    val time = System.currentTimeMillis() / 100f
    repeat(20) { i ->
        val x = (size.width * Random(i).nextFloat())
        val y = (size.height * Random(i * 2).nextFloat())
        val alpha = 0.6f * (0.5f + 0.5f * sin(time + i * 0.5f))

        drawCircle(
            color = Color.Yellow.copy(alpha = alpha),
            radius = 3f,
            center = Offset(x, y)
        )
    }
}

data class AnimatedStar(
    val x: Float,
    val y: Float,
    val brightness: Float,
    val size: Float,
    val twinkleSpeed: Float
)

data class BackgroundPlanet(
    val initialX: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)
package com.students.spacegame.models

import androidx.compose.ui.graphics.Color

data class Particle(
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float,
    var life: Float,
    val maxLife: Float,
    val color: Color,
    val size: Float
) {
    val isAlive: Boolean get() = life > 0f

    fun update() {
        x += velocityX
        y += velocityY
        life -= 0.016f // 60 FPS
        velocityY += 0.1f // Гравитация
    }
}

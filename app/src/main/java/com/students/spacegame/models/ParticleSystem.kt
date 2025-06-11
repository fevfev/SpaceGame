package com.students.spacegame.models


import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random



object ParticleFactory {
    fun createExplosion(x: Float, y: Float, color: Color = Color.Yellow): List<Particle> {
        return List(15) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 100f + 50f

            Particle(
                x = x,
                y = y,
                velocityX = (cos(angle) * speed).toFloat(),
                velocityY = (sin(angle) * speed).toFloat(),
                life = 1f,
                maxLife = 1f,
                color = color,
                size = Random.nextFloat() * 4f + 2f
            )
        }
    }

    fun createBonusPickup(x: Float, y: Float): List<Particle> {
        return List(8) {
            Particle(
                x = x,
                y = y,
                velocityX = Random.nextFloat() * 60f - 30f,
                velocityY = Random.nextFloat() * 60f - 30f,
                life = 0.8f,
                maxLife = 0.8f,
                color = Color.Yellow,
                size = 3f
            )
        }
    }
}

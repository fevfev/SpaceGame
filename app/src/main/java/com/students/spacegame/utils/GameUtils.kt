package com.students.spacegame.utils

import androidx.compose.ui.geometry.Offset
import com.students.spacegame.R
import kotlin.math.sqrt
import kotlin.random.Random

object GameUtils {
    fun checkCollision(
        obj1X: Float, obj1Y: Float, obj1Radius: Float,
        obj2X: Float, obj2Y: Float, obj2Radius: Float
    ): Boolean {
        val distance = sqrt((obj1X - obj2X) * (obj1X - obj2X) + (obj1Y - obj2Y) * (obj1Y - obj2Y))
        return distance < (obj1Radius + obj2Radius)
    }

    fun isOffScreen(x: Float, y: Float, screenWidth: Float, screenHeight: Float): Boolean {
        return x < -50f || x > screenWidth + 50f || y < -50f || y > screenHeight + 50f
    }

    fun generateRandomPosition(screenWidth: Float): Offset {
        return Offset(
            x = Random.nextFloat() * (screenWidth - 100f) + 50f,
            y = -50f
        )
    }
}



package com.students.spacegame.models

import androidx.compose.ui.graphics.Color

enum class EnemyType {
    ASTEROID, BIG_ASTEROID, ENEMY_SHIP
}

data class Enemy(
    val id: Int,
    val type: EnemyType,
    var x: Float,
    var y: Float,
    val speed: Float,
    var health: Int,
    val size: Float
) {
    val color: Color
        get() = when (type) {
            EnemyType.ASTEROID -> Color.Gray
            EnemyType.BIG_ASTEROID -> Color.Red
            EnemyType.ENEMY_SHIP -> Color.Magenta
        }

    val points: Int
        get() = when (type) {
            EnemyType.ASTEROID -> 10
            EnemyType.BIG_ASTEROID -> 25
            EnemyType.ENEMY_SHIP -> 50
        }
}

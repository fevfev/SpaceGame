package com.students.spacegame.models

enum class DifficultyLevel(
    val displayName: String,
    val enemySpeedMultiplier: Float,
    val enemyHealthMultiplier: Float,
    val scoreMultiplier: Float,
    val enemySpawnRate: Float
) {
    EASY("Легкий", 0.2f, 0.2f, 0.2f, 0.2f),
    NORMAL("Нормальный", 1f, 1f, 1f, 1f),
    HARD("Сложный", 1.3f, 1.5f, 1.5f, 1.3f),
    NIGHTMARE("Кошмар", 1.6f, 2f, 2f, 1.6f)
}

data class GameSettings(
    val difficulty: DifficultyLevel = DifficultyLevel.NORMAL,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

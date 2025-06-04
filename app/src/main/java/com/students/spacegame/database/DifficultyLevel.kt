package com.students.spacegame.database

enum class DifficultyLevel {
    EASY(enemySpeedMultiplier = 0.7f, enemyHealthMultiplier = 0.8f),
    NORMAL(enemySpeedMultiplier = 1f, enemyHealthMultiplier = 1f),
    HARD(enemySpeedMultiplier = 1.3f, enemyHealthMultiplier = 1.5f),
    NIGHTMARE(enemySpeedMultiplier = 1.6f, enemyHealthMultiplier = 2f);

    constructor(enemySpeedMultiplier: Float, enemyHealthMultiplier: Float) {
        this.enemySpeedMultiplier = enemySpeedMultiplier
        this.enemyHealthMultiplier = enemyHealthMultiplier
    }

    val enemySpeedMultiplier: Float
    val enemyHealthMultiplier: Float
}

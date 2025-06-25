package com.students.spacegame.database

enum class DifficultyLevel {
    EASY(enemySpeedMultiplier = 0.2f, enemyHealthMultiplier = 0.1f),
    NORMAL(enemySpeedMultiplier = 0.5f, enemyHealthMultiplier = 0.5f),
    HARD(enemySpeedMultiplier = 1f, enemyHealthMultiplier = 1f),
    NIGHTMARE(enemySpeedMultiplier = 1.6f, enemyHealthMultiplier = 2f);

    constructor(enemySpeedMultiplier: Float, enemyHealthMultiplier: Float) {
        this.enemySpeedMultiplier = enemySpeedMultiplier
        this.enemyHealthMultiplier = enemyHealthMultiplier
    }

    val enemySpeedMultiplier: Float
    val enemyHealthMultiplier: Float
}

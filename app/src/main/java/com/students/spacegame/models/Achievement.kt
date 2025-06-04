package com.students.spacegame.models

enum class AchievementType {
    FIRST_KILL,
    SCORE_1000,
    SCORE_5000,
    SCORE_10000,
    UNLOCK_ALL_SHIPS,
    BOSS_DEFEATED,
    PERFECT_WAVE,
    WEAPON_MASTER,
    CREDITS_EARNED,
    COMBO_MASTER
}

data class Achievement(
    val type: AchievementType,
    val title: String,
    val description: String,
    val reward: Int,
    val isUnlocked: Boolean = false
) {
    companion object {
        fun getAllAchievements(): List<Achievement> = listOf(
            Achievement(
                type = AchievementType.FIRST_KILL,
                title = "Первая кровь",
                description = "Уничтожьте первого врага",
                reward = 50
            ),
            Achievement(
                type = AchievementType.SCORE_1000,
                title = "Начинающий пилот",
                description = "Наберите 1000 очков",
                reward = 100
            ),
            Achievement(
                type = AchievementType.SCORE_5000,
                title = "Опытный боец",
                description = "Наберите 5000 очков",
                reward = 250
            ),
            Achievement(
                type = AchievementType.SCORE_10000,
                title = "Мастер космоса",
                description = "Наберите 10000 очков",
                reward = 500
            ),
            Achievement(
                type = AchievementType.UNLOCK_ALL_SHIPS,
                title = "Коллекционер",
                description = "Разблокируйте все корабли",
                reward = 1000
            ),
            Achievement(
                type = AchievementType.BOSS_DEFEATED,
                title = "Убийца боссов",
                description = "Победите любого босса",
                reward = 300
            ),
            Achievement(
                type = AchievementType.PERFECT_WAVE,
                title = "Совершенство",
                description = "Пройдите волну без урона",
                reward = 200
            ),
            Achievement(
                type = AchievementType.WEAPON_MASTER,
                title = "Мастер оружия",
                description = "Используйте все виды оружия",
                reward = 400
            )
        )
    }
}

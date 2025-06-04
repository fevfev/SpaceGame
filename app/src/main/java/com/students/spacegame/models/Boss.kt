package com.students.spacegame.models

enum class BossType {
    DESTROYER,
    MOTHERSHIP
}

data class Boss(
    val type: BossType,
    val name: String,
    var x: Float,
    var y: Float,
    var health: Int,
    val maxHealth: Int,
    val size: Float,
    val attackPatterns: List<AttackPattern>,
    val imageName: String,
    var currentPhase: Int = 1
) {
    val isDefeated: Boolean get() = health <= 0

    companion object {
        fun createBoss(type: BossType, screenWidth: Float): Boss {
            return when (type) {
                BossType.DESTROYER -> Boss(
                    type = type,
                    name = "Звездный разрушитель",
                    x = screenWidth / 2,
                    y = -200f,
                    health = 50,
                    maxHealth = 50,
                    size = 80f,
                    attackPatterns = listOf(
                        AttackPattern.SPREAD_FIRE,
                        AttackPattern.LASER_BEAM,
                        AttackPattern.MISSILE_BARRAGE
                    ),
                    imageName = "boss_destroyer.png"
                )
                BossType.MOTHERSHIP -> Boss(
                    type = type,
                    name = "Материнский корабль",
                    x = screenWidth / 2,
                    y = -300f,
                    health = 100,
                    maxHealth = 100,
                    size = 120f,
                    attackPatterns = listOf(
                        AttackPattern.SPAWN_MINIONS,
                        AttackPattern.DEATH_RAY,
                        AttackPattern.TELEPORT_ATTACK,
                        AttackPattern.SHIELD_PHASE
                    ),
                    imageName = "boss_mothership.png"
                )
            }
        }
    }
}

enum class AttackPattern {
    SPREAD_FIRE,      // Веер выстрелов
    LASER_BEAM,       // Лазерный луч
    MISSILE_BARRAGE,  // Залп ракет
    SPAWN_MINIONS,    // Спавн миньонов
    DEATH_RAY,        // Луч смерти
    TELEPORT_ATTACK,  // Телепорт атака
    SHIELD_PHASE      // Фаза щита
}

package com.students.spacegame.models

enum class UpgradeType {
    HEALTH_BOOST,
    SPEED_BOOST,
    FIRE_RATE,
    DAMAGE_MULTIPLIER
}

data class ShipUpgrade(
    val shipType: ShipType,
    val upgradeType: UpgradeType,
    val level: Int,
    val cost: Int,
    val effect: Float
)

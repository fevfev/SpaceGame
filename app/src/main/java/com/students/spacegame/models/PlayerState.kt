package com.students.spacegame.models



data class PlayerState(
    var x: Float = 400f,
    var y: Float = 800f,
    var health: Int = 3,
    var weapon: WeaponType = WeaponType.LASER,
    var hasShield: Boolean = false,
    var isInvincible: Boolean = false,
    var hasSpeedBoost: Boolean = false,
    val size: Float = 40f
)


package com.students.spacegame.models

import androidx.compose.ui.graphics.Color

enum class BonusType {
    SHIELD, SPEED, WEAPON, INVINCIBILITY
}

data class Bonus(
    val id: Int,
    val type: BonusType,
    var x: Float,
    var y: Float,
    val size: Float = 30f
) {
    val color: Color
        get() = when (type) {
            BonusType.SHIELD -> Color.Blue
            BonusType.SPEED -> Color.Yellow
            BonusType.WEAPON -> Color.Magenta
            BonusType.INVINCIBILITY -> Color.Green
        }
}

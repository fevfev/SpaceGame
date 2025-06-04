package com.students.spacegame.models

import androidx.compose.ui.graphics.Color

enum class ExplosionType {
    SMALL,      // Маленький взрыв
    MEDIUM,     // Средний взрыв
    LARGE,      // Большой взрыв
    FREEZE,     // Ледяной эффект
    LIGHTNING,  // Электрический эффект
    NUCLEAR     // Ядерный взрыв
}

data class Explosion(
    val id: Int,
    var x: Float,
    var y: Float,
    val type: ExplosionType,
    var animationFrame: Int = 0,
    val maxFrames: Int = 10,
    val imageName: String
) {
    val isFinished: Boolean get() = animationFrame >= maxFrames

    companion object {
        fun create(x: Float, y: Float, type: ExplosionType, id: Int): Explosion {
            return Explosion(
                id = id,
                x = x,
                y = y,
                type = type,
                imageName = when (type) {
                    ExplosionType.SMALL -> "explosion_small.png"
                    ExplosionType.MEDIUM -> "explosion_medium.png"
                    ExplosionType.LARGE -> "explosion_large.png"
                    ExplosionType.FREEZE -> "explosion_freeze.png"
                    ExplosionType.LIGHTNING -> "explosion_lightning.png"
                    ExplosionType.NUCLEAR -> "explosion_nuclear.png"
                }
            )
        }
    }
}
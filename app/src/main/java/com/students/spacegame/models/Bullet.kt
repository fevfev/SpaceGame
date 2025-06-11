package com.students.spacegame.models


import androidx.compose.ui.graphics.Color

data class Bullet(
    val id: Int,
    var x: Float,
    var y: Float,
    val speed: Float = 10f,
    val size: Float = 8f,
    val weaponType: WeaponType = WeaponType.LASER,
    val imageName: String = getBulletImage(WeaponType.LASER),
    val angle: Float = 0f
) {
    companion object {
        fun getBulletImage(weaponType: WeaponType): String {
            return when (weaponType) {
                WeaponType.LASER -> "bullet_laser.png"
                WeaponType.PLASMA -> "bullet_plasma.png"
                WeaponType.RAIL_GUN -> "bullet_railgun.png"
                WeaponType.MISSILE -> "bullet_missile.png"
                WeaponType.SPREAD_SHOT -> "bullet_spread.png"
                WeaponType.LIGHTNING -> "bullet_lightning.png"
                WeaponType.FREEZE_RAY -> "bullet_freeze.png"
                WeaponType.NUKE -> "bullet_nuke.png"
            }
        }
    }
}

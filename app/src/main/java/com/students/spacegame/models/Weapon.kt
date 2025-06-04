package com.students.spacegame.models

import androidx.compose.ui.graphics.Color

/**
 * Типы оружия для игры Space Warriors.
 * Подробнее о enum class: https://kotlinlang.org/docs/enum-classes.html
 */
enum class WeaponType {
    LASER,          // Стандартный
    PLASMA,         // Медленный, мощный
    RAIL_GUN,       // Пробивающий
    MISSILE,        // Самонаводящийся
    SPREAD_SHOT,    // Веер выстрелов
    LIGHTNING,      // Цепной урон
    FREEZE_RAY,     // Замедляет врагов
    NUKE            // Взрыв области
}

/**
 * WeaponUpgrade описывает тип улучшения оружия, которое может быть получено при сборе бонуса.
 * Подробнее о enum class: https://kotlinlang.org/docs/enum-classes.html
 */
enum class WeaponUpgrade {
    NONE,           // Без улучшения
    SPREAD,         // Веерный выстрел
    HOMING,         // Автонаведение
    RAPID_FIRE,     // Быстрая стрельба
    MULTISHOT,      // Много выстрелов
    POWER_SHOT      // Усиленный урон
}


data class Weapon(
    val type: WeaponType,
    val name: String,
    val damage: Int,
    val fireRate: Float,
    val bulletSpeed: Float,
    val specialEffect: String,
    val bulletColor: Color,
    val imageName: String,
    val level: Int = 1,
    val upgrade: WeaponUpgrade? = null // Добавлено поле upgrade
) {
    companion object {
        fun getAllWeapons(): List<Weapon> = listOf(
            Weapon(
                type = WeaponType.LASER,
                name = "Лазер",
                damage = 1,
                fireRate = 0.3f,
                bulletSpeed = 12f,
                specialEffect = "Базовое оружие",
                bulletColor = Color.Yellow,
                imageName = "weapon_laser.png"
            ),
            Weapon(
                type = WeaponType.PLASMA,
                name = "Плазма",
                damage = 3,
                fireRate = 0.8f,
                bulletSpeed = 8f,
                specialEffect = "Мощный урон",
                bulletColor = Color.Magenta,
                imageName = "weapon_plasma.png"
            ),
            Weapon(
                type = WeaponType.RAIL_GUN,
                name = "Рейлган",
                damage = 5,
                fireRate = 1.5f,
                bulletSpeed = 20f,
                specialEffect = "Пробивает врагов",
                bulletColor = Color.Cyan,
                imageName = "weapon_railgun.png"
            ),
            Weapon(
                type = WeaponType.MISSILE,
                name = "Ракеты",
                damage = 4,
                fireRate = 1.0f,
                bulletSpeed = 6f,
                specialEffect = "Самонаведение",
                bulletColor = Color.Red,
                imageName = "weapon_missile.png"
            ),
            Weapon(
                type = WeaponType.SPREAD_SHOT,
                name = "Дробовик",
                damage = 2,
                fireRate = 0.6f,
                bulletSpeed = 10f,
                specialEffect = "3 выстрела веером",
                bulletColor = Color(0xFFFFA500), // Orange
                imageName = "weapon_spread.png"
            ),
            Weapon(
                type = WeaponType.LIGHTNING,
                name = "Молния",
                damage = 2,
                fireRate = 0.4f,
                bulletSpeed = 15f,
                specialEffect = "Поражает 3 цели",
                bulletColor = Color.Blue,
                imageName = "weapon_lightning.png"
            ),
            Weapon(
                type = WeaponType.FREEZE_RAY,
                name = "Заморозка",
                damage = 1,
                fireRate = 0.3f,
                bulletSpeed = 12f,
                specialEffect = "Замедляет врагов",
                bulletColor = Color.White,
                imageName = "weapon_freeze.png"
            ),
            Weapon(
                type = WeaponType.NUKE,
                name = "Ядерка",
                damage = 10,
                fireRate = 3.0f,
                bulletSpeed = 8f,
                specialEffect = "Взрыв области",
                bulletColor = Color.Green,
                imageName = "weapon_nuke.png"
            )
        )
    }
}


package com.students.spacegame.models

enum class ShipType {
    FIGHTER,    // Быстрый, маневренный
    TANK,       // Много HP, медленный
    SNIPER,     // Дальний бой, точный
    GUNSHIP,    // Двойные выстрелы
    STEALTH     // Невидимость, критурон
}

data class Ship(
    val type: ShipType,
    val name: String,
    val maxHealth: Int,
    val speed: Float,
    val fireRate: Float,
    val specialAbility: String,
    val imageName: String,
    val price: Int
) {
    companion object {
        fun getAllShips(): List<Ship> = listOf(
            Ship(
                type = ShipType.FIGHTER,
                name = "XÆ A-12",
                maxHealth = 3,
                speed = 8f,
                fireRate = 0.3f,
                specialAbility = "Быстрое уклонение",
                imageName = "ship_fighter.png",
                price = 0
            ),
            Ship(
                type = ShipType.TANK,
                name = "Мк-II",
                maxHealth = 8,
                speed = 4f,
                fireRate = 0.8f,
                specialAbility = "Броня +50%",
                imageName = "ship_tank.png",
                price = 500
            ),
            Ship(
                type = ShipType.SNIPER,
                name = "Зет",
                maxHealth = 4,
                speed = 6f,
                fireRate = 1.2f,
                specialAbility = "Пробивающие выстрелы",
                imageName = "ship_sniper.png",
                price = 750
            ),
            Ship(
                type = ShipType.GUNSHIP,
                name = "∀",
                maxHealth = 5,
                speed = 5f,
                fireRate = 0.4f,
                specialAbility = "Двойные выстрелы",
                imageName = "ship_gunship.png",
                price = 1000
            ),
            Ship(
                type = ShipType.STEALTH,
                name = "AGE-2",
                maxHealth = 2,
                speed = 9f,
                fireRate = 0.5f,
                specialAbility = "Невидимость 3 сек",
                imageName = "ship_stealth.png",
                price = 1500
            )
        )
    }
}

package com.students.spacegame.models

enum class GameStatus {
    MENU, SHIP_SELECT, WEAPON_SELECT, PLAYING, PAUSED, BOSS_FIGHT, GAME_OVER, VICTORY
}

data class GameState(
    val status: GameStatus = GameStatus.MENU,
    val score: Int = 0,
    val credits: Int = 0,
    val currentZone: Zone = Zone.getAllZones().first(),
    val selectedShip: Ship = Ship.getAllShips().first(),
    val player: PlayerState = PlayerState(),
    val currentWeapon: Weapon = Weapon.getAllWeapons().first(),
    val availableWeapons: List<Weapon> = listOf(Weapon.getAllWeapons().first()),
    val enemies: List<Enemy> = emptyList(),
    val bonuses: List<Bonus> = emptyList(),
    val bullets: List<Bullet> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val currentBoss: Boss? = null,
    val waveNumber: Int = 1,
    val killCount: Int = 0,
    val comboMultiplier: Int = 1,
    val gameTime: Long = 0
)
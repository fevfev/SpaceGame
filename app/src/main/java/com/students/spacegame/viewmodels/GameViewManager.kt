package com.students.spacegame.viewmodels

import com.students.spacegame.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameViewManager @Inject constructor() {

    private val _selectedShip = MutableStateFlow(Ship.getAllShips().first())
    val selectedShip: StateFlow<Ship> = _selectedShip

    private val _selectedWeapon = MutableStateFlow(Weapon.getAllWeapons().first())
    val selectedWeapon: StateFlow<Weapon> = _selectedWeapon

    private val _playerCredits = MutableStateFlow(1000)
    val playerCredits: StateFlow<Int> = _playerCredits

    private val _unlockedShips = MutableStateFlow(setOf(Ship.getAllShips().first().type))
    val unlockedShips: StateFlow<Set<ShipType>> = _unlockedShips

    private val _unlockedWeapons = MutableStateFlow(
        setOf(WeaponType.LASER, WeaponType.PLASMA, WeaponType.RAIL_GUN)
    )

    // НАСТРОЙКИ ИГРЫ
    private val _gameSettings = MutableStateFlow(GameSettings())
    val gameSettings: StateFlow<GameSettings> = _gameSettings

    // ДОСТИЖЕНИЯ
    private val _achievements = MutableStateFlow(Achievement.getAllAchievements())
    val achievements: StateFlow<List<Achievement>> = _achievements

    // АПГРЕЙДЫ КОРАБЛЕЙ
    private val _shipUpgrades = MutableStateFlow(
        mapOf<ShipType, Map<UpgradeType, Int>>()
    )
    val shipUpgrades: StateFlow<Map<ShipType, Map<UpgradeType, Int>>> = _shipUpgrades

    fun selectShip(ship: Ship) {
        println("🚀 GameViewManager.selectShip: ${ship.name} (${ship.type})")
        if (_unlockedShips.value.contains(ship.type) || ship.price == 0) {
            _selectedShip.value = ship
            println("✅ Корабль выбран: ${ship.name}")
        }
    }

    fun purchaseShip(ship: Ship) {
        if (_playerCredits.value >= ship.price && !_unlockedShips.value.contains(ship.type)) {
            _playerCredits.value -= ship.price
            _unlockedShips.value = _unlockedShips.value + ship.type
            _selectedShip.value = ship
            println("🛒 Корабль куплен: ${ship.name} за ${ship.price} кредитов")
        }
    }

    fun selectWeapon(weapon: Weapon) {
        if (_unlockedWeapons.value.contains(weapon.type)) {
            _selectedWeapon.value = weapon
        }
    }

    fun addCredits(amount: Int) {
        val oldCredits = _playerCredits.value
        _playerCredits.value += amount
        val newCredits = _playerCredits.value

        println("💰 КРЕДИТЫ: было $oldCredits, добавили +$amount, стало $newCredits")
    }

    fun getAvailableWeapons(): List<Weapon> {
        return Weapon.getAllWeapons().filter { weapon ->
            _unlockedWeapons.value.contains(weapon.type)
        }
    }

    fun unlockWeapon(weaponType: WeaponType) {
        _unlockedWeapons.value = _unlockedWeapons.value + weaponType
        println("🔓 Разблокировано оружие: ${weaponType.name}")
    }

    // НАСТРОЙКИ
    fun updateDifficulty(difficulty: DifficultyLevel) {
        _gameSettings.value = _gameSettings.value.copy(difficulty = difficulty)
    }

    fun toggleSound() {
        _gameSettings.value = _gameSettings.value.copy(soundEnabled = !_gameSettings.value.soundEnabled)
    }

    fun toggleMusic() {
        _gameSettings.value = _gameSettings.value.copy(musicEnabled = !_gameSettings.value.musicEnabled)
    }

    fun toggleVibration() {
        _gameSettings.value = _gameSettings.value.copy(vibrationEnabled = !_gameSettings.value.vibrationEnabled)
    }

    // АПГРЕЙДЫ
    fun purchaseUpgrade(shipType: ShipType, upgradeType: UpgradeType) {
        val currentUpgrades = _shipUpgrades.value.toMutableMap()
        val shipUpgrades = currentUpgrades[shipType]?.toMutableMap() ?: mutableMapOf()
        val currentLevel = shipUpgrades[upgradeType] ?: 0
        val cost = calculateUpgradeCost(upgradeType, currentLevel + 1)

        if (_playerCredits.value >= cost && currentLevel < 5) {
            _playerCredits.value -= cost
            shipUpgrades[upgradeType] = currentLevel + 1
            currentUpgrades[shipType] = shipUpgrades
            _shipUpgrades.value = currentUpgrades
            println("⚡ Апгрейд куплен: ${upgradeType.name} уровень ${currentLevel + 1}")
        }
    }

    private fun calculateUpgradeCost(type: UpgradeType, level: Int): Int {
        val baseCost = when (type) {
            UpgradeType.HEALTH_BOOST -> 100
            UpgradeType.SPEED_BOOST -> 150
            UpgradeType.FIRE_RATE -> 200
            UpgradeType.DAMAGE_MULTIPLIER -> 250
        }
        return baseCost * level
    }

    fun getGameConfiguration(): GameConfiguration {
        return GameConfiguration(
            selectedShip = _selectedShip.value,
            selectedWeapon = _selectedWeapon.value,
            playerCredits = _playerCredits.value
        )
    }

    fun checkAchievements(gameState: GameState) {
        val updated = _achievements.value.map { achievement ->
            if (achievement.isUnlocked) return@map achievement
            when (achievement.type) {
                AchievementType.FIRST_KILL ->
                    if (gameState.score > 0) achievement.copy(isUnlocked = true) else achievement
                AchievementType.SCORE_1000 ->
                    if (gameState.score >= 1000) achievement.copy(isUnlocked = true) else achievement
                AchievementType.SCORE_5000 ->
                    if (gameState.score >= 5000) achievement.copy(isUnlocked = true) else achievement
                AchievementType.SCORE_10000 ->
                    if (gameState.score >= 10000) achievement.copy(isUnlocked = true) else achievement
                AchievementType.UNLOCK_ALL_SHIPS ->
                    if (unlockedShips.value.size == Ship.getAllShips().size) achievement.copy(isUnlocked = true) else achievement
                AchievementType.BOSS_DEFEATED ->
                    if (gameState.currentBoss == null && gameState.status == GameStatus.BOSS_FIGHT) achievement.copy(isUnlocked = true) else achievement
                AchievementType.PERFECT_WAVE ->
                    if (gameState.player.health == gameState.selectedShip.maxHealth && gameState.status == GameStatus.PLAYING) achievement.copy(isUnlocked = true) else achievement
                AchievementType.WEAPON_MASTER ->
                    if (_unlockedWeapons.value.size == WeaponType.entries.size) achievement.copy(isUnlocked = true) else achievement
                AchievementType.CREDITS_EARNED ->
                    if (_playerCredits.value >= 1000) achievement.copy(isUnlocked = true) else achievement
                AchievementType.COMBO_MASTER ->
                    achievement // Добавьте свою логику для комбо
            }
        }
        _achievements.value = updated
    }
}

data class GameConfiguration(
    val selectedShip: Ship,
    val selectedWeapon: Weapon,
    val playerCredits: Int
)
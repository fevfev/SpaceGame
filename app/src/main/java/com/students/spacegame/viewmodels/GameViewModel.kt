package com.students.spacegame.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.students.spacegame.database.GameScore
import com.students.spacegame.database.GameScoreDao
import com.students.spacegame.models.*
import com.students.spacegame.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject
import com.students.spacegame.components.SoundManager
import kotlin.math.cos
import kotlin.math.sin

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameScoreDao: GameScoreDao,
    private val supabaseRepository: SupabaseRepository,
    private val gameViewManager: GameViewManager,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private var comboCount = 0
    private var lastKillTime = 0L

    fun onEnemyKilled() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastKillTime < 3000) { // 3 секунды на комбо
            comboCount++
            addScore(baseScore * comboCount) // Множитель очков
        } else {
            comboCount = 1
        }
        lastKillTime = currentTime
    }

    private var nextEnemyId = 0
    private var nextBulletId = 0
    private var nextBonusId = 0
    private var lastShootTime = 0L

    init {
        startGame()
    }

    /**
     * Пример: в updateBullets реализуем эффекты улучшений оружия.
     * Подробнее: https://kotlinlang.org/docs/enum-classes.html
     */
    private fun updateBullets(currentState: GameState): List<Bullet> {
        return currentState.bullets.map { bullet ->
            // Движение с учётом angle (угла)
            val angleRad = Math.toRadians(bullet.angle.toDouble())
            val dx = (sin(angleRad) * bullet.speed).toFloat()
            val dy = -(cos(angleRad) * bullet.speed).toFloat() // вверх по экрану
            bullet.copy(x = bullet.x + dx, y = bullet.y + dy)
        }.filter { it.y > -50f }
    }

    fun startGame() {
        val config = gameViewManager.getGameConfiguration()
        println("Запуск игры с кораблем: ${config.selectedShip.name}, оружием: ${config.selectedWeapon.name}")

        _gameState.value = GameState(
            status = GameStatus.PLAYING,
            selectedShip = config.selectedShip,
            currentWeapon = config.selectedWeapon,
            credits = config.playerCredits,
            player = PlayerState(
                health = config.selectedShip.maxHealth,
                weapon = config.selectedWeapon.type,
                x = 400f,
                y = 800f,
                size = 40f
            )
        )
    }

    fun movePlayerLeft() {
        val currentState = _gameState.value
        val newX = (currentState.player.x - currentState.selectedShip.speed * 5f).coerceAtLeast(50f)
        _gameState.value = currentState.copy(
            player = currentState.player.copy(x = newX)
        )
    }

    fun movePlayerRight() {
        val currentState = _gameState.value
        val newX = (currentState.player.x + currentState.selectedShip.speed * 5f).coerceAtMost(750f)
        _gameState.value = currentState.copy(
            player = currentState.player.copy(x = newX)
        )
    }

    private fun addScore(points: Int) {
        val currentState = _gameState.value
        _gameState.value = currentState.copy(
            score = currentState.score + points
        )
    }

    private val baseScore = 50


    fun togglePause() {
        val currentState = _gameState.value
        _gameState.value = currentState.copy(
            status = when (currentState.status) {
                GameStatus.PLAYING -> GameStatus.PAUSED
                GameStatus.BOSS_FIGHT -> GameStatus.PAUSED
                GameStatus.PAUSED -> {
                    if (currentState.currentBoss != null) GameStatus.BOSS_FIGHT
                    else GameStatus.PLAYING
                }
                else -> currentState.status
            }
        )
    }

    fun updateGame() {
        val currentState = _gameState.value
        if (currentState.status != GameStatus.PLAYING && currentState.status != GameStatus.BOSS_FIGHT) return

        // Обновление позиций врагов с учетом зоны
        val enemySpeedMultiplier = currentState.currentZone.enemySpeed
        val updatedEnemies = currentState.enemies.map { enemy ->
            enemy.copy(y = enemy.y + enemy.speed * enemySpeedMultiplier)
        }.filter { it.y < 1000f }

        // Обновление позиций пуль с учетом оружия и upgrade
        val updatedBullets = updateBullets(currentState)

        // Обновление позиций бонусов
        val updatedBonuses = currentState.bonuses.map { bonus ->
            bonus.copy(y = bonus.y + 3f)
        }.filter { it.y < 1000f }

        // Обновление босса
        var currentBoss = currentState.currentBoss
        currentBoss?.let { boss ->
            boss.y += 1f
            if (boss.y > 100f) boss.y = 100f
        }

        // Спавн новых врагов
        val newEnemies = if (currentBoss == null && Random.nextFloat() < currentState.currentZone.enemySpawnRate) {
            updatedEnemies + createRandomEnemy()
        } else updatedEnemies

        // Спавн новых бонусов
        val newBonuses = if (Random.nextFloat() < 0.008f) {
            updatedBonuses + createRandomBonus()
        } else updatedBonuses

        // Проверка столкновений пуль с врагами
        var newScore = currentState.score
        var finalEnemies = newEnemies.toMutableList()
        var finalBullets = updatedBullets.toMutableList()
        var newPlayer = currentState.player

        val bulletsToRemove = mutableListOf<Bullet>()
        val enemiesToRemove = mutableListOf<Enemy>()

        finalBullets.forEach { bullet ->
            finalEnemies.forEach { enemy ->
                if (checkCollision(bullet.x, bullet.y, enemy.x, enemy.y, 30f)) {
                    bulletsToRemove.add(bullet)

                    // Урон зависит от выбранного оружия
                    val damage = currentState.currentWeapon.damage
                    println("Урон оружием ${currentState.currentWeapon.name}: $damage")

                    val updatedEnemy = enemy.copy(health = enemy.health - damage)
                    if (updatedEnemy.health <= 0) {
                        enemiesToRemove.add(enemy)
                        newScore += enemy.points

                        // Бонус кредитов
                        gameViewManager.addCredits(enemy.points / 10)
                    } else {
                        finalEnemies[finalEnemies.indexOf(enemy)] = updatedEnemy
                    }

                    // Специальные эффекты оружия
                    when (currentState.currentWeapon.type) {
                        WeaponType.RAIL_GUN -> {
                            // Пробивает врагов - не убираем пулю
                        }
                        WeaponType.SPREAD_SHOT -> {
                            // Обычная логика
                        }
                        WeaponType.LIGHTNING -> {
                            // Поражает ближайших врагов
                            finalEnemies.filter {
                                it != enemy && checkCollision(enemy.x, enemy.y, it.x, it.y, 100f)
                            }.take(2).forEach { nearbyEnemy ->
                                val lightningDamage = finalEnemies.indexOf(nearbyEnemy)
                                if (lightningDamage >= 0) {
                                    finalEnemies[lightningDamage] = nearbyEnemy.copy(health = nearbyEnemy.health - 1)
                                }
                            }
                        }
                        else -> {
                            // Стандартная логика
                        }
                    }
                }
            }

            // Столкновения с боссом
            currentBoss?.let { boss ->
                if (checkCollision(bullet.x, bullet.y, boss.x, boss.y, boss.size)) {
                    bulletsToRemove.add(bullet)
                    boss.health -= currentState.currentWeapon.damage
                    if (boss.isDefeated) {
                        newScore += 1000
                        gameViewManager.addCredits(100)
                        currentBoss = null
                    }
                }
            }
        }

        // Убираем пули (кроме рейлгана)
        if (currentState.currentWeapon.type != WeaponType.RAIL_GUN) {
            finalBullets.removeAll(bulletsToRemove)
        }
        finalEnemies.removeAll(enemiesToRemove)

        // Проверка столкновений игрока с бонусами
        val bonusesToRemove = mutableListOf<Bonus>()
        newBonuses.forEach { bonus ->
            if (checkCollision(newPlayer.x, newPlayer.y, bonus.x, bonus.y, 40f)) {
                bonusesToRemove.add(bonus)
                newPlayer = applyBonus(newPlayer, bonus.type)
            }
        }

        val finalBonuses = newBonuses - bonusesToRemove.toSet()

        // Проверка спавна босса
        if (currentBoss == null && newScore > 0 && newScore % 3000 == 0) {
            currentBoss = Boss.createBoss(BossType.DESTROYER, 800f)
        }

        // Проверка смены зоны
        val newZone = Zone.getAllZones().lastOrNull { it.requiredScore <= newScore }
            ?: currentState.currentZone

        // Проверка Game Over
        val gameOver = finalEnemies.any { it.y >= newPlayer.y - 50f } ||
                newPlayer.health <= 0

        _gameState.value = currentState.copy(
            enemies = finalEnemies,
            bullets = finalBullets,
            bonuses = finalBonuses,
            score = newScore,
            player = newPlayer,
            currentBoss = currentBoss,
            currentZone = newZone,
            status = when {
                gameOver -> GameStatus.GAME_OVER
                currentBoss != null -> GameStatus.BOSS_FIGHT
                else -> GameStatus.PLAYING
            }
        )

        if (gameOver) {
            saveScore(newScore)
        }
    }

    private fun applyBonus(player: PlayerState, bonusType: BonusType): PlayerState {
        return when (bonusType) {
            BonusType.SHIELD -> {
                gameViewManager.addCredits(20)
                soundManager.playSound("bonus_pickup")
                player.copy(hasShield = true)
            }
            BonusType.SPEED -> {
                gameViewManager.addCredits(15)
                soundManager.playSound("bonus_pickup")
                player.copy(hasSpeedBoost = true)
            }
            BonusType.WEAPON -> {
                gameViewManager.addCredits(50)
                soundManager.playSound("bonus_pickup")
                val allWeapons = Weapon.getAllWeapons()
                val newWeapon = allWeapons.random()
                gameViewManager.unlockWeapon(newWeapon.type)
                player
            }
            BonusType.INVINCIBILITY -> {
                gameViewManager.addCredits(30)
                soundManager.playSound("bonus_pickup")
                player.copy(isInvincible = true)
            }
        }
    }

    fun movePlayer(x: Float) {
        val currentState = _gameState.value

        _gameState.value = currentState.copy(
            player = currentState.player.copy(x = x.coerceIn(50f, 750f))
        )
    }

    fun shoot() {
        val currentState = _gameState.value
        if (currentState.status != GameStatus.PLAYING && currentState.status != GameStatus.BOSS_FIGHT) return

        val currentTime = System.currentTimeMillis()
        val fireRate = currentState.selectedShip.fireRate * currentState.currentWeapon.fireRate

        // Проверяем скорострельность
        if (currentTime - lastShootTime < fireRate * 1000) return

        lastShootTime = currentTime
        val weapon = currentState.currentWeapon

        // Если есть upgrade, применяем его эффекты при выстреле
        when (weapon.upgrade) {
            WeaponUpgrade.SPREAD -> {
                val bullets = listOf(
                    createBullet(currentState.player.x - 20f, currentState.player.y - 50f, weapon).copy(angle = -15f),
                    createBullet(currentState.player.x, currentState.player.y - 50f, weapon).copy(angle = 0f),
                    createBullet(currentState.player.x + 20f, currentState.player.y - 50f, weapon).copy(angle = 15f)
                )
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + bullets
                )
            }
            WeaponUpgrade.MULTISHOT -> {
                val bullets = listOf(
                    createBullet(currentState.player.x - 40f, currentState.player.y - 50f, weapon).copy(angle = -25f),
                    createBullet(currentState.player.x - 20f, currentState.player.y - 50f, weapon).copy(angle = -12f),
                    createBullet(currentState.player.x, currentState.player.y - 50f, weapon).copy(angle = 0f),
                    createBullet(currentState.player.x + 20f, currentState.player.y - 50f, weapon).copy(angle = 12f),
                    createBullet(currentState.player.x + 40f, currentState.player.y - 50f, weapon).copy(angle = 25f)
                )
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + bullets
                )
            }
            WeaponUpgrade.RAPID_FIRE -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weapon)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            WeaponUpgrade.HOMING -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weapon)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            WeaponUpgrade.POWER_SHOT -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weapon)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            else -> {
                // Если нет upgrade, используем стандартную логику
                when (weapon.type) {
                    WeaponType.SPREAD_SHOT -> {
                        val bullets = listOf(
                            createBullet(currentState.player.x - 20f, currentState.player.y - 50f, weapon),
                            createBullet(currentState.player.x, currentState.player.y - 50f, weapon),
                            createBullet(currentState.player.x + 20f, currentState.player.y - 50f, weapon)
                        )
                        _gameState.value = currentState.copy(
                            bullets = currentState.bullets + bullets
                        )
                    }
                    else -> {
                        val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weapon)
                        _gameState.value = currentState.copy(
                            bullets = currentState.bullets + newBullet
                        )
                    }
                }
            }
        }
    }

    private fun createBullet(x: Float, y: Float, weapon: Weapon): Bullet {
        return Bullet(
            id = nextBulletId++,
            x = x,
            y = y,
            speed = weapon.bulletSpeed,
            size = when (weapon.type) {
                WeaponType.RAIL_GUN -> 12f
                WeaponType.NUKE -> 15f
                else -> 8f
            }
        )
    }

    private fun createRandomEnemy(): Enemy {
        val type = EnemyType.entries.toTypedArray().random()
        return Enemy(
            id = nextEnemyId++,
            type = type,
            x = Random.nextFloat() * 700f + 50f,
            y = -50f,
            speed = when (type) {
                EnemyType.ASTEROID -> 3f
                EnemyType.BIG_ASTEROID -> 2f
                EnemyType.ENEMY_SHIP -> 4f
            },
            health = when (type) {
                EnemyType.ASTEROID -> 1
                EnemyType.BIG_ASTEROID -> 3
                EnemyType.ENEMY_SHIP -> 2
            },
            size = when (type) {
                EnemyType.ASTEROID -> 25f
                EnemyType.BIG_ASTEROID -> 40f
                EnemyType.ENEMY_SHIP -> 30f
            }
        )
    }

    private fun createRandomBonus(): Bonus {
        return Bonus(
            id = nextBonusId++,
            type = BonusType.entries.toTypedArray().random(),
            x = Random.nextFloat() * 700f + 50f,
            y = -30f
        )
    }

    private fun checkCollision(x1: Float, y1: Float, x2: Float, y2: Float, threshold: Float): Boolean {
        val dx = x1 - x2
        val dy = y1 - y2
        return (dx * dx + dy * dy) < (threshold * threshold)
    }

    private fun saveScore(score: Int) {
        viewModelScope.launch {
            val gameScore = GameScore(
                playerName = "Player",
                score = score,
                level = 1
            )

            gameScoreDao.insert(gameScore)
            supabaseRepository.saveScore(gameScore)
        }
    }
}


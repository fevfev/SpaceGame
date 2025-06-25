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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

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

    private var speedBoostJob: Job? = null
    private var weaponBonusJob: Job? = null
    private var invincibilityJob: Job? = null
    private var originalWeapon: WeaponType? = null

    fun onEnemyKilled() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastKillTime < 3000) { // 3 секунды на комбо
            comboCount++
            addScore(baseScore * comboCount) // Множитель очков
        } else {
            comboCount = 1
        }
        lastKillTime = currentTime
        gameViewManager.checkAchievements(_gameState.value)
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
        val speedMultiplier = if (currentState.player.hasSpeedBoost) 1.7f else 1f
        val newX = (currentState.player.x - currentState.selectedShip.speed * 5f * speedMultiplier).coerceAtLeast(50f)
        _gameState.value = currentState.copy(
            player = currentState.player.copy(x = newX)
        )
    }

    fun movePlayerRight() {
        val currentState = _gameState.value
        val speedMultiplier = if (currentState.player.hasSpeedBoost) 1.7f else 1f
        val newX = (currentState.player.x + currentState.selectedShip.speed * 5f * speedMultiplier).coerceAtMost(750f)
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
        val maxEnemySpeed = 8f // Ограничение максимальной скорости
        val updatedEnemies = currentState.enemies.map { enemy ->
            val speed = (enemy.speed * enemySpeedMultiplier).coerceAtMost(maxEnemySpeed)
            enemy.copy(y = enemy.y + speed)
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
        var playerDamaged = false
        var playerShieldUsed = false
        var playerInvincible = false
        val enemiesHitPlayer = mutableSetOf<Enemy>()
        for (enemy in finalEnemies) {
            if (checkCollision(newPlayer.x, newPlayer.y, enemy.x, enemy.y, newPlayer.size + enemy.size)) {
                if (newPlayer.isInvincible) {
                    playerInvincible = true
                    continue
                } else if (newPlayer.hasShield) {
                    playerShieldUsed = true
                } else {
                    playerDamaged = true
                }
                enemiesHitPlayer.add(enemy)
            }
        }
        if (playerInvincible) {
            // ничего не делаем
        } else if (playerShieldUsed) {
            newPlayer = newPlayer.copy(hasShield = false)
        } else if (playerDamaged) {
            newPlayer = newPlayer.copy(health = newPlayer.health - 1)
        }
        // Удаляем врагов, столкнувшихся с игроком
        val filteredEnemies = finalEnemies.filter { it !in enemiesHitPlayer }
        finalEnemies = filteredEnemies.toMutableList()

        // Обработка столкновений с бонусами (applyBonus)
        val bonusesToRemove = mutableListOf<Bonus>()
        for (bonus in newBonuses) {
            if (checkCollision(newPlayer.x, newPlayer.y, bonus.x, bonus.y, 40f)) {
                bonusesToRemove.add(bonus)
                newPlayer = applyBonus(newPlayer, bonus.type)
            }
        }
        // После этого newPlayer больше не меняется до записи в GameState!

        // Проверка спавна босса
        if (currentBoss == null && newScore > 0 && newScore % 3000 == 0) {
            currentBoss = Boss.createBoss(BossType.DESTROYER, 800f)
        }

        // Проверка смены зоны
        val newZone = Zone.getAllZones().lastOrNull { it.requiredScore <= newScore }
            ?: currentState.currentZone

        // Проверка Game Over
        val gameOver = newPlayer.health <= 0

        // Проверка столкновений пуль с врагами
        val bulletsToRemove = mutableSetOf<Bullet>()
        val enemiesToRemove = mutableSetOf<Enemy>()
        finalBullets.forEach { bullet ->
            var bulletHit = false
            for (enemy in finalEnemies) {
                if (checkCollision(bullet.x, bullet.y, enemy.x, enemy.y, 30f)) {
                    // Урон зависит от выбранного оружия
                    val damage = currentState.currentWeapon.damage
                    val updatedEnemy = enemy.copy(health = enemy.health - damage)
                    if (updatedEnemy.health <= 0 && !enemiesToRemove.contains(enemy)) {
                        enemiesToRemove.add(enemy)
                        newScore += enemy.points
                        gameViewManager.addCredits(enemy.points / 10)
                        onEnemyKilled() // ВАЖНО: вызываем для комбо
                    } else if (updatedEnemy.health > 0) {
                        finalEnemies[finalEnemies.indexOf(enemy)] = updatedEnemy
                    }
                    // Пули (кроме рейлгана) исчезают после первого попадания
                    if (currentState.currentWeapon.type != WeaponType.RAIL_GUN) {
                        bulletsToRemove.add(bullet)
                        bulletHit = true
                        break
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

        _gameState.value = currentState.copy(
            enemies = finalEnemies,
            bullets = finalBullets,
            bonuses = newBonuses - bonusesToRemove.toSet(),
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
            gameViewManager.checkAchievements(_gameState.value)
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
                speedBoostJob?.cancel()
                if (!player.hasSpeedBoost) {
                    speedBoostJob = viewModelScope.launch {
                        delay(5000)
                        _gameState.value = _gameState.value.copy(
                            player = _gameState.value.player.copy(hasSpeedBoost = false)
                        )
                    }
                }
                player.copy(hasSpeedBoost = true)
            }
            BonusType.WEAPON -> {
                gameViewManager.addCredits(50)
                soundManager.playSound("bonus_pickup")
                weaponBonusJob?.cancel()
                val original = player.weapon
                if (player.weapon != WeaponType.SPREAD_SHOT) {
                    weaponBonusJob = viewModelScope.launch {
                        delay(10000)
                        _gameState.value = _gameState.value.copy(
                            player = _gameState.value.player.copy(weapon = original)
                        )
                    }
                }
                player.copy(weapon = WeaponType.SPREAD_SHOT)
            }
            BonusType.INVINCIBILITY -> {
                gameViewManager.addCredits(30)
                soundManager.playSound("bonus_pickup")
                invincibilityJob?.cancel()
                if (!player.isInvincible) {
                    invincibilityJob = viewModelScope.launch {
                        delay(5000)
                        _gameState.value = _gameState.value.copy(
                            player = _gameState.value.player.copy(isInvincible = false)
                        )
                    }
                }
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
        if (currentTime - lastShootTime < fireRate * 1000) return
        lastShootTime = currentTime
        val weaponParams = currentState.currentWeapon
        val weaponType = currentState.player.weapon
        when (weaponType) {
            WeaponType.SPREAD_SHOT -> {
                val bullets = listOf(
                    createBullet(currentState.player.x - 20f, currentState.player.y - 50f, weaponParams).copy(angle = -15f, size = 14f),
                    createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams).copy(angle = 0f, size = 14f),
                    createBullet(currentState.player.x + 20f, currentState.player.y - 50f, weaponParams).copy(angle = 15f, size = 14f)
                )
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + bullets
                )
            }
            WeaponType.RAIL_GUN -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            WeaponType.MISSILE -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            WeaponType.LIGHTNING -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            WeaponType.FREEZE_RAY -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            WeaponType.NUKE -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
            }
            else -> {
                val newBullet = createBullet(currentState.player.x, currentState.player.y - 50f, weaponParams)
                _gameState.value = currentState.copy(
                    bullets = currentState.bullets + newBullet
                )
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


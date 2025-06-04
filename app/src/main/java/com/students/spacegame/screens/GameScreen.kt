package com.students.spacegame.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.students.spacegame.R
import com.students.spacegame.components.BossHealthBar
import com.students.spacegame.components.DynamicBackground
import com.students.spacegame.components.GameButton
import com.students.spacegame.components.GameHUD
import com.students.spacegame.components.SoundManager
import com.students.spacegame.di.SoundManagerEntryPoint
import com.students.spacegame.models.GameState
import com.students.spacegame.models.GameStatus
import com.students.spacegame.models.WeaponType
import com.students.spacegame.viewmodels.GameViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun GameScreen(
    onGameEnd: (Int) -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val context = LocalContext.current
    val soundManager: SoundManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SoundManagerEntryPoint::class.java
        ).soundManager()
    }

    val (isAutoFireEnabled, setAutoFireEnabled) = remember { mutableStateOf(false) }

    // Загрузка изображений
    val shipFighter = ImageBitmap.imageResource(R.drawable.ship_fighter)
    val shipTank = ImageBitmap.imageResource(R.drawable.ship_tank)
    val shipSniper = ImageBitmap.imageResource(R.drawable.ship_sniper)
    val shipGunship = ImageBitmap.imageResource(R.drawable.ship_gunship)
    val shipStealth = ImageBitmap.imageResource(R.drawable.ship_stealth)

    val bulletLaser = ImageBitmap.imageResource(R.drawable.bullet_laser)
    val bulletPlasma = ImageBitmap.imageResource(R.drawable.bullet_plasma)
    val bulletRailgun = ImageBitmap.imageResource(R.drawable.bullet_railgun)
    val bulletMissile = ImageBitmap.imageResource(R.drawable.bullet_missile)
    val bulletSpread = ImageBitmap.imageResource(R.drawable.bullet_spread)
    val bulletLightning = ImageBitmap.imageResource(R.drawable.bullet_lightning)
    val bulletFreeze = ImageBitmap.imageResource(R.drawable.bullet_freeze)
    val bulletNuke = ImageBitmap.imageResource(R.drawable.bullet_nuke)

    val enemyAsteroid = ImageBitmap.imageResource(R.drawable.enemy_asteroid)
    val enemyBigAsteroid = ImageBitmap.imageResource(R.drawable.enemy_big_asteroid)
    val enemyShip = ImageBitmap.imageResource(R.drawable.enemy_ship)

    val bonusShield = ImageBitmap.imageResource(R.drawable.bonus_shield)
    val bonusSpeed = ImageBitmap.imageResource(R.drawable.bonus_speed)
    val bonusWeapon = ImageBitmap.imageResource(R.drawable.bonus_weapon)
    val bonusInvincibility = ImageBitmap.imageResource(R.drawable.bonus_invincibility)

    LaunchedEffect(gameState.status) {
        when (gameState.status) {
            GameStatus.GAME_OVER -> {
                soundManager.playSound("defeat")
                soundManager.vibrate(500)
                onGameEnd(gameState.score)
            }
            GameStatus.BOSS_FIGHT -> {
                soundManager.playSound("boss_spawn")
                soundManager.vibrate(200)
            }
            else -> {}
        }
    }

    // Музыка по зонам
    LaunchedEffect(gameState.currentZone.id) {
        soundManager.playBackgroundMusic(gameState.currentZone.id)
    }

    // Игровой цикл
    LaunchedEffect(gameState.status, isAutoFireEnabled) {
        while (gameState.status == GameStatus.PLAYING || gameState.status == GameStatus.BOSS_FIGHT) {
            viewModel.updateGame()
            if (isAutoFireEnabled) {
                viewModel.shoot()
                soundManager.playWeaponSound(gameState.currentWeapon.type)
            }
            delay(16)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            DynamicBackground(
                currentZone = gameState.currentZone,
                modifier = Modifier.fillMaxSize()
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            if (gameState.status == GameStatus.PLAYING || gameState.status == GameStatus.BOSS_FIGHT) {
                                viewModel.movePlayer(change.position.x * 800f / size.width)
                            }
                        }
                    }
            ) {
                val virtualHeight = 1000f
                val virtualWidth = 800f
                val scaleY = size.height / virtualHeight
                val scaleX = size.width / virtualWidth

                // ИГРОК
                val playerImage = when (gameState.selectedShip.type.name) {
                    "FIGHTER" -> shipFighter
                    "TANK" -> shipTank
                    "SNIPER" -> shipSniper
                    "GUNSHIP" -> shipGunship
                    "STEALTH" -> shipStealth
                    else -> shipFighter
                }

                drawImage(
                    image = playerImage,
                    dstOffset = IntOffset(
                        ((gameState.player.x - gameState.player.size) * scaleX).toInt(),
                        ((gameState.player.y - gameState.player.size) * scaleY).toInt()
                    ),
                    dstSize = IntSize(
                        (gameState.player.size * 2 * scaleX).toInt(),
                        (gameState.player.size * 2 * scaleY).toInt()
                    )
                )

                // ВРАГИ
                gameState.enemies.forEach { enemy ->
                    val enemyImage = when (enemy.type.name) {
                        "ASTEROID" -> enemyAsteroid
                        "BIG_ASTEROID" -> enemyBigAsteroid
                        "ENEMY_SHIP" -> enemyShip
                        else -> enemyAsteroid
                    }

                    drawImage(
                        image = enemyImage,
                        dstOffset = IntOffset(
                            ((enemy.x - enemy.size) * scaleX).toInt(),
                            ((enemy.y - enemy.size) * scaleY).toInt()
                        ),
                        dstSize = IntSize(
                            (enemy.size * 2 * scaleX).toInt(),
                            (enemy.size * 2 * scaleY).toInt()
                        )
                    )
                }

                // ПУЛИ
                gameState.bullets.forEach { bullet ->
                    val bulletImage = when (gameState.currentWeapon.type) {
                        WeaponType.LASER -> bulletLaser
                        WeaponType.PLASMA -> bulletPlasma
                        WeaponType.RAIL_GUN -> bulletRailgun
                        WeaponType.MISSILE -> bulletMissile
                        WeaponType.SPREAD_SHOT -> bulletSpread
                        WeaponType.LIGHTNING -> bulletLightning
                        WeaponType.FREEZE_RAY -> bulletFreeze
                        WeaponType.NUKE -> bulletNuke
                    }

                    drawImage(
                        image = bulletImage,
                        dstOffset = IntOffset(
                            ((bullet.x - bullet.size) * scaleX).toInt(),
                            ((bullet.y - bullet.size) * scaleY).toInt()
                        ),
                        dstSize = IntSize(
                            (bullet.size * 2 * scaleX).toInt(),
                            (bullet.size * 2 * scaleY).toInt()
                        )
                    )
                }

                // БОНУСЫ с анимацией
                gameState.bonuses.forEach { bonus ->
                    val bonusImage = when (bonus.type.name) {
                        "SHIELD" -> bonusShield
                        "SPEED" -> bonusSpeed
                        "WEAPON" -> bonusWeapon
                        "INVINCIBILITY" -> bonusInvincibility
                        else -> bonusShield
                    }

                    val pulseScale = 1f + 0.2f * sin(System.currentTimeMillis() * 0.005f + bonus.id).toFloat()

                    drawImage(
                        image = bonusImage,
                        dstOffset = IntOffset(
                            ((bonus.x - bonus.size * pulseScale) * scaleX).toInt(),
                            ((bonus.y - bonus.size * pulseScale) * scaleY).toInt()
                        ),
                        dstSize = IntSize(
                            (bonus.size * 2 * pulseScale * scaleX).toInt(),
                            (bonus.size * 2 * pulseScale * scaleY).toInt()
                        )
                    )
                }

                // ЧАСТИЦЫ
                gameState.particles.forEach { particle ->
                    drawCircle(
                        color = particle.color.copy(alpha = particle.life / particle.maxLife),
                        radius = particle.size * scaleX,
                        center = Offset(particle.x * scaleX, particle.y * scaleY)
                    )
                }

                // БОСС
                gameState.currentBoss?.let { boss ->
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset((boss.x - boss.size) * scaleX, (boss.y - boss.size / 2) * scaleY),
                        size = Size(boss.size * 2 * scaleX, boss.size * scaleY)
                    )

                    // Энергетический щит босса
                    drawCircle(
                        color = Color.Magenta.copy(alpha = 0.3f),
                        radius = boss.size * 1.2f * scaleX,
                        center = Offset(boss.x * scaleX, boss.y * scaleY)
                    )
                }
            }

            // UI элементы поверх игрового поля
            GameHUD(
                score = gameState.score,
                health = gameState.player.health,
                level = gameState.currentZone.id,
                credits = gameState.credits,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )

            // Босс здоровье
            gameState.currentBoss?.let { boss ->
                BossHealthBar(
                    boss = boss,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }

            // Информация об оружии
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔫 ${gameState.currentWeapon.name}",
                        color = gameState.currentWeapon.bulletColor,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "🚀 ${gameState.selectedShip.name}",
                        color = Color.Cyan,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "🌌 ${gameState.currentZone.name}",
                        color = Color.Yellow,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Wave: ${gameState.waveNumber}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // Кнопка паузы и автоогня — теперь всегда поверх и по центру
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Кнопка автоогня
                ImageButton(
                    onClick = { setAutoFireEnabled(!isAutoFireEnabled) },
                    imageRes = if (isAutoFireEnabled) R.drawable.btn_auto_fire_on else R.drawable.btn_auto_fire_off,
                    size = 48.dp,
                    description = "Автоогонь"
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Кнопка паузы
                ImageButton(
                    onClick = {
                        soundManager.playSound("button_click")
                        viewModel.togglePause()
                    },
                    imageRes = if (gameState.status == GameStatus.PAUSED) R.drawable.btn_play else R.drawable.btn_pause,
                    size = 48.dp,
                    description = "Пауза"
                )
            }

            // Экран паузы
            if (gameState.status == GameStatus.PAUSED) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⏸️ ПАУЗА", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            GameButton(
                                text = "▶️ ПРОДОЛЖИТЬ",
                                onClick = {
                                    soundManager.playSound("button_click")
                                    viewModel.togglePause()
                                }
                            )
                        }
                    }
                }
            }
        }

        GameControlPanel(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            gameState = gameState,
            onMoveLeft = {
                viewModel.movePlayerLeft()
                soundManager.vibrate(50)
            },
            onMoveRight = {
                viewModel.movePlayerRight()
                soundManager.vibrate(50)
            },
            onFire = {
                viewModel.shoot()
                soundManager.playWeaponSound(gameState.currentWeapon.type)
                soundManager.vibrate(30)
            },
            onPause = {
                soundManager.playSound("button_click")
                viewModel.togglePause()
            },
            showPauseButton = true,
            isAutoFireEnabled = isAutoFireEnabled,
            onToggleAutoFire = { setAutoFireEnabled(!isAutoFireEnabled) }
        )
    }
}

@Composable
fun GameControlPanel(
    modifier: Modifier = Modifier,
    gameState: GameState,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onFire: () -> Unit,
    onPause: () -> Unit,
    showPauseButton: Boolean = true,
    isAutoFireEnabled: Boolean = false,
    onToggleAutoFire: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Мини-статусы
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStatCard("❤️", gameState.player.health.toString(), Color.Red)
                MiniStatCard("🎯", gameState.score.toString(), Color.Yellow)
                MiniStatCard("⚡", gameState.waveNumber.toString(), Color.Cyan)
                MiniStatCard("💰", gameState.credits.toString(), Color.Green)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageButton(
                    onClick = onToggleAutoFire,
                    imageRes = if (isAutoFireEnabled) R.drawable.btn_auto_fire_on else R.drawable.btn_auto_fire_off,
                    size = 48.dp,
                    description = "Автоогонь"
                )
                ImageButton(
                    onClick = onMoveLeft,
                    imageRes = R.drawable.btn_move_left,
                    size = 64.dp,
                    description = "Влево"
                )
                ImageButton(
                    onClick = onFire,
                    imageRes = R.drawable.btn_fire,
                    size = 80.dp,
                    description = "Огонь"
                )
                ImageButton(
                    onClick = onMoveRight,
                    imageRes = R.drawable.btn_move_right,
                    size = 64.dp,
                    description = "Вправо"
                )
                ImageButton(
                    onClick = onPause,
                    imageRes = if (gameState.status == GameStatus.PAUSED) R.drawable.btn_play else R.drawable.btn_pause,
                    size = 48.dp,
                    description = "Пауза"
                )
            }
        }
    }
}

@Composable
fun ImageButton(
    onClick: () -> Unit,
    imageRes: Int,
    size: androidx.compose.ui.unit.Dp,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(size)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = description,
                modifier = Modifier.size(size * 0.6f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun MiniStatCard(icon: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

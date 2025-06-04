package com.students.spacegame.screens


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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.students.spacegame.di.GameViewManagerEntryPoint
import com.students.spacegame.di.SoundManagerEntryPoint
import com.students.spacegame.models.DifficultyLevel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val gameViewManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            GameViewManagerEntryPoint::class.java
        ).gameViewManager()
    }

    val soundManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SoundManagerEntryPoint::class.java
        ).soundManager()
    }

    val gameSettings by gameViewManager.gameSettings.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMenuBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            soundManager.playSound("button_click")
                            onBackClick()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "⚙️ НАСТРОЙКИ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                // СЛОЖНОСТЬ
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "🎯 СЛОЖНОСТЬ",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DifficultyLevel.entries.forEach { difficulty ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = gameSettings.difficulty == difficulty,
                                        onClick = {
                                            soundManager.playSound("button_click")
                                            gameViewManager.updateDifficulty(difficulty)
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = gameSettings.difficulty == difficulty,
                                    onClick = {
                                        soundManager.playSound("button_click")
                                        gameViewManager.updateDifficulty(difficulty)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = difficulty.displayName,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Множитель очков: ${difficulty.scoreMultiplier}x",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Сложность врагов: ${(difficulty.enemyHealthMultiplier * 100).toInt()}%",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                // ЗВУК И МУЗЫКА
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "🔊 ЗВУК И МУЗЫКА",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SettingsToggle(
                            title = "Звуковые эффекты",
                            checked = gameSettings.soundEnabled,
                            onToggle = {
                                soundManager.playSound("button_click")
                                gameViewManager.toggleSound()
                                soundManager.updateSoundEnabled(gameSettings.soundEnabled)
                            }
                        )

                        SettingsToggle(
                            title = "Фоновая музыка",
                            checked = gameSettings.musicEnabled,
                            onToggle = {
                                soundManager.playSound("button_click")
                                val newValue = !gameSettings.musicEnabled
                                gameViewManager.toggleMusic()
                                soundManager.updateMusicEnabled(newValue)
                            }
                        )

                        SettingsToggle(
                            title = "Вибрация",
                            checked = gameSettings.vibrationEnabled,
                            onToggle = {
                                soundManager.playSound("button_click")
                                gameViewManager.toggleVibration()
                                soundManager.updateVibrationEnabled(gameSettings.vibrationEnabled)
                            }
                        )
                    }
                }
            }
            item {
                // ИНФОРМАЦИЯ О ИГРЕ
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ℹ️ О ИГРЕ",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Space Warriors v1.0", color = Color.Gray, fontSize = 14.sp)
                        Text("Разработано для изучения Android", color = Color.Gray, fontSize = 14.sp)
                        Text("Используется Jetpack Compose", color = Color.Gray, fontSize = 14.sp)
                        Text("Room + Supabase Backend", color = Color.Gray, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "🚀 Классическая космическая аркада",
                            color = Color.Cyan,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsToggle(
    title: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Cyan,
                checkedTrackColor = Color.Cyan.copy(alpha = 0.5f)
            )
        )
    }
}


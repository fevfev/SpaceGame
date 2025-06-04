package com.students.spacegame.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.students.spacegame.components.GameButton
import com.students.spacegame.di.GameViewManagerEntryPoint
import com.students.spacegame.models.UpgradeType
import dagger.hilt.android.EntryPointAccessors

@Composable
fun UpgradeScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val gameViewManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            GameViewManagerEntryPoint::class.java
        ).gameViewManager()
    }

    val playerCredits by gameViewManager.playerCredits.collectAsState()
    val shipUpgrades by gameViewManager.shipUpgrades.collectAsState()
    val selectedShip by gameViewManager.selectedShip.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMenuBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBackClick()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }
                Text(
                    text = "⚙️ АПГРЕЙДЫ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "💰 $playerCredits",
                    color = Color.Yellow,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Информация о выбранном корабле
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚀 ${selectedShip.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan
                    )
                    Text(
                        text = "Выберите улучшения для вашего корабля",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список апгрейдов
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(UpgradeType.entries.toTypedArray()) { upgradeType ->
                    val currentLevel = shipUpgrades[selectedShip.type]?.get(upgradeType) ?: 0
                    val nextLevel = currentLevel + 1
                    val cost = calculateUpgradeCost(upgradeType, nextLevel)
                    val maxLevel = 5

                    UpgradeCard(
                        upgradeType = upgradeType,
                        currentLevel = currentLevel,
                        nextLevel = nextLevel,
                        cost = cost,
                        maxLevel = maxLevel,
                        playerCredits = playerCredits,
                        onPurchase = {
                            gameViewManager.purchaseUpgrade(selectedShip.type, upgradeType)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UpgradeCard(
    upgradeType: UpgradeType,
    currentLevel: Int,
    nextLevel: Int,
    cost: Int,
    maxLevel: Int,
    playerCredits: Int,
    onPurchase: () -> Unit
) {
    val canAfford = playerCredits >= cost && currentLevel < maxLevel

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford)
                Color.Black.copy(alpha = 0.8f)
            else
                Color.Red.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${getUpgradeIcon(upgradeType)} ${getUpgradeName(upgradeType)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = getUpgradeDescription(upgradeType),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Прогресс апгрейда
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Уровень: ", color = Color.White, fontSize = 12.sp)
                    repeat(maxLevel) { i ->
                        Text(
                            text = if (i < currentLevel) "★" else "☆",
                            color = if (i < currentLevel) Color.Yellow else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (currentLevel < maxLevel) {
                GameButton(
                    text = if (canAfford) "💰 $cost" else "🔒 $cost",
                    onClick = if (canAfford) onPurchase else { {} },
                    modifier = Modifier.width(100.dp)
                )
            } else {
                Text(
                    text = "МАКС",
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun getUpgradeIcon(type: UpgradeType): String = when (type) {
    UpgradeType.HEALTH_BOOST -> "❤️"
    UpgradeType.SPEED_BOOST -> "⚡"
    UpgradeType.FIRE_RATE -> "🔫"
    UpgradeType.DAMAGE_MULTIPLIER -> "💥"
}

fun getUpgradeName(type: UpgradeType): String = when (type) {
    UpgradeType.HEALTH_BOOST -> "Усиление корпуса"
    UpgradeType.SPEED_BOOST -> "Улучшенные двигатели"
    UpgradeType.FIRE_RATE -> "Система охлаждения"
    UpgradeType.DAMAGE_MULTIPLIER -> "Энергоусилитель"
}

fun getUpgradeDescription(type: UpgradeType): String = when (type) {
    UpgradeType.HEALTH_BOOST -> "Увеличивает прочность корабля на 1 единицу"
    UpgradeType.SPEED_BOOST -> "Повышает скорость передвижения на 10%"
    UpgradeType.FIRE_RATE -> "Ускоряет перезарядку оружия на 15%"
    UpgradeType.DAMAGE_MULTIPLIER -> "Увеличивает урон оружия на 25%"
}

fun calculateUpgradeCost(type: UpgradeType, level: Int): Int {
    val baseCost = when (type) {
        UpgradeType.HEALTH_BOOST -> 100
        UpgradeType.SPEED_BOOST -> 150
        UpgradeType.FIRE_RATE -> 200
        UpgradeType.DAMAGE_MULTIPLIER -> 250
    }
    return baseCost * level
}

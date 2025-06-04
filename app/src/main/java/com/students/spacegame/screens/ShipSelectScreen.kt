package com.students.spacegame.screens

import com.students.spacegame.R
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.students.spacegame.components.DynamicBackground
import com.students.spacegame.components.GameButton
import com.students.spacegame.di.GameViewManagerEntryPoint
import com.students.spacegame.models.Ship
import com.students.spacegame.models.ShipType
import com.students.spacegame.models.Zone
import dagger.hilt.android.EntryPointAccessors

@Composable
fun ShipSelectScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val gameViewManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            GameViewManagerEntryPoint::class.java
        ).gameViewManager()
    }

    val selectedShip by gameViewManager.selectedShip.collectAsState()
    val playerCredits by gameViewManager.playerCredits.collectAsState()
    val unlockedShips by gameViewManager.unlockedShips.collectAsState()
    val availableShips = Ship.getAllShips()

    var showPurchaseDialog by remember { mutableStateOf<Ship?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        DynamicBackground(
            currentZone = Zone.getAllZones().first(),
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onBack()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🚀 АНГАР",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Cyan
                        )
                        Text(
                            text = "Выберите корабль",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Yellow.copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💰",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "$playerCredits",
                                color = Color.Yellow,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  КАРТОЧКА КОРАБЛЯ
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val pulseAlpha by rememberInfiniteTransition(label = "pulse").animateFloat(
                            initialValue = 0.5f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_alpha"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    3.dp,
                                    getShipColor(selectedShip.type).copy(alpha = pulseAlpha),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                ShipImage(
                                    shipType = selectedShip.type,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = "ВЫБРАН",
                                    fontSize = 10.sp,
                                    color = Color.Cyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedShip.name,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "✨ ${selectedShip.specialAbility}",
                            color = Color.Cyan,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Характеристики с прогресс-барами
                        StatProgressBar("❤️ Здоровье", selectedShip.maxHealth, 10, Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        StatProgressBar("⚡ Скорость", selectedShip.speed.toInt(), 10, Color.Green)
                        Spacer(modifier = Modifier.height(8.dp))
                        StatProgressBar("🔫 Огневая мощь", (10/selectedShip.fireRate).toInt(), 10, Color.Yellow)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Заголовок списка кораблей
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛸 ДОСТУПНЫЕ КОРАБЛИ",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Разблокировано: ${unlockedShips.size}/${availableShips.size}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Список кораблей
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                itemsIndexed(availableShips) { index, ship ->
                    val isUnlocked = unlockedShips.contains(ship.type) || ship.price == 0
                    val canAfford = playerCredits >= ship.price

                    EnhancedShipCard(
                        ship = ship,
                        isSelected = ship.type == selectedShip.type,
                        isUnlocked = isUnlocked,
                        canAfford = canAfford,
                        onClick = {
                            if (isUnlocked) {
                                gameViewManager.selectShip(ship)
                            } else if (canAfford) {
                                showPurchaseDialog = ship
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Кнопка продолжить
            GameButton(
                text = "⚔️ ВЫБРАТЬ ОРУЖИЕ",
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ДИАЛОГ ПОКУПКИ
        showPurchaseDialog?.let { ship ->
            PurchaseDialog(
                ship = ship,
                playerCredits = playerCredits,
                onConfirm = {
                    gameViewManager.purchaseShip(ship)
                    showPurchaseDialog = null
                },
                onDismiss = { showPurchaseDialog = null }
            )
        }
    }
}

@Composable
fun StatProgressBar(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(80.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            // Фон прогресс-бара
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
            // Заполнение прогресс-бара
            Box(
                modifier = Modifier
                    .fillMaxWidth(value.toFloat() / maxValue.toFloat())
                    .fillMaxHeight()
                    .background(color)
            )
        }

        Text(
            text = "$value",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
fun EnhancedShipCard(
    ship: Ship,
    isSelected: Boolean,
    isUnlocked: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> getShipColor(ship.type).copy(alpha = 0.3f)
                isUnlocked -> Color.Black.copy(alpha = 0.8f)
                canAfford -> Color.Blue.copy(alpha = 0.2f)
                else -> Color.Red.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 6.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // РЕАЛЬНОЕ ИЗОБРАЖЕНИЕ КОРАБЛЯ
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(getShipImageResource(ship.type)),
                    contentDescription = ship.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (isUnlocked) 1f else 0.5f),
                    colorFilter = if (!isUnlocked)
                        ColorFilter.tint(Color.Gray) else null
                )

                // Эффект свечения для выбранного
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Color.Cyan, RoundedCornerShape(8.dp))
                    )
                }
            }

            Text(
                text = ship.name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            // Статус корабля
            when {
                isSelected -> {
                    Text("✅ ВЫБРАН", fontSize = 10.sp, color = Color.Cyan)
                }
                isUnlocked -> {
                    Text("🔓 ДОСТУПЕН", fontSize = 10.sp, color = Color.Green)
                }
                canAfford -> {
                    Text("💰 ${ship.price}", fontSize = 10.sp, color = Color.Yellow)
                }
                else -> {
                    Text("🔒 ${ship.price}", fontSize = 10.sp, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun getShipImageResource(shipType: ShipType): Int {
    return when (shipType) {
        ShipType.FIGHTER -> R.drawable.ship_fighter
        ShipType.TANK -> R.drawable.ship_tank
        ShipType.SNIPER -> R.drawable.ship_sniper
        ShipType.GUNSHIP -> R.drawable.ship_gunship
        ShipType.STEALTH -> R.drawable.ship_stealth
    }
}

@Composable
fun ShipImage(shipType: ShipType, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(getShipImageResource(shipType)),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun PurchaseDialog(
    ship: Ship,
    playerCredits: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🚀 Покупка корабля")
        },
        text = {
            Column {
                Text("Вы хотите купить ${ship.name}?")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Цена: ${ship.price} кредитов")
                Text("У вас: $playerCredits кредитов")
                Text("Останется: ${playerCredits - ship.price} кредитов")
            }
        },
        confirmButton = {
            GameButton(
                text = "КУПИТЬ",
                onClick = onConfirm
            )
        },
        dismissButton = {
            GameButton(
                text = "ОТМЕНА",
                onClick = onDismiss
            )
        }
    )
}

fun getShipColor(shipType: ShipType): Color {
    return when (shipType) {
        ShipType.FIGHTER -> Color.Cyan
        ShipType.TANK -> Color.Green
        ShipType.SNIPER -> Color.Yellow
        ShipType.GUNSHIP -> Color.Magenta
        ShipType.STEALTH -> Color.White
    }
}

// @Composable
// fun getShipIcon(shipType: ShipType): String {
//     return when (shipType) {
//         ShipType.FIGHTER -> "⚡"
//         ShipType.TANK -> "🛡️"
//         ShipType.SNIPER -> "🎯"
//         ShipType.GUNSHIP -> "🔫"
//         ShipType.STEALTH -> "👻"
//     }
// }


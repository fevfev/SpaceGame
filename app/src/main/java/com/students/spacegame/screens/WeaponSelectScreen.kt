package com.students.spacegame.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.students.spacegame.components.GameButton
import com.students.spacegame.di.GameViewManagerEntryPoint
import com.students.spacegame.models.Weapon
import com.students.spacegame.models.WeaponType
import dagger.hilt.android.EntryPointAccessors

@Composable
fun WeaponSelectScreen(
    onStartGame: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val gameViewManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            GameViewManagerEntryPoint::class.java
        ).gameViewManager()
    }

    val selectedWeapon: Weapon by gameViewManager.selectedWeapon.collectAsState()
    val availableWeapons: List<Weapon> = gameViewManager.getAvailableWeapons()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedMenuBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
                Text(
                    text = "🔫 ВЫБОР ОРУЖИЯ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(120.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Информация о выбранном оружии
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Выбранное оружие:",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🔫 ${selectedWeapon.name}",
                        color = selectedWeapon.bulletColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✨ ${selectedWeapon.specialEffect}",
                        color = Color.Gray
                    )
                    Row {
                        Text("💥 Урон: ${selectedWeapon.damage}", color = Color.Red)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("⚡ Скорость: ${selectedWeapon.bulletSpeed.toInt()}", color = Color.Green)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(availableWeapons) { index, weapon ->
                    WeaponCard(
                        weapon = weapon,
                        isSelected = weapon == selectedWeapon,
                        onClick = { gameViewManager.selectWeapon(weapon) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GameButton(
                text = "🎮 НАЧАТЬ ИГРУ",
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WeaponCard(
    weapon: Weapon,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                weapon.bulletColor.copy(alpha = 0.3f)
            else
                Color.Black.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getWeaponEmoji(weapon.type),
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = weapon.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = weapon.specialEffect,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text("💥 ${weapon.damage}", color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("⚡ ${weapon.bulletSpeed.toInt()}", color = Color.Green, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("🔄 ${(1/weapon.fireRate).toInt()}/с", color = Color.Yellow, fontSize = 12.sp)
                }
            }

            if (isSelected) {
                Text("✅", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun getWeaponEmoji(weaponType: WeaponType): String {
    return when (weaponType) {
        WeaponType.LASER -> "⚡"
        WeaponType.PLASMA -> "🔥"
        WeaponType.RAIL_GUN -> "🎯"
        WeaponType.MISSILE -> "🚀"
        WeaponType.SPREAD_SHOT -> "🌟"
        WeaponType.LIGHTNING -> "⚡"
        WeaponType.FREEZE_RAY -> "❄️"
        WeaponType.NUKE -> "💣"
    }
}
package com.students.spacegame.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.students.spacegame.components.GameButton
import com.students.spacegame.di.GameViewManagerEntryPoint
import com.students.spacegame.models.*
import dagger.hilt.android.EntryPointAccessors

@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val gameViewManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            GameViewManagerEntryPoint::class.java
        ).gameViewManager()
    }

    val achievements by gameViewManager.achievements.collectAsState()

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
                    text = "🏅 ДОСТИЖЕНИЯ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${achievements.count { it.isUnlocked }}/${achievements.size}",
                    color = Color.Yellow,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Прогресс достижений
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val progress = achievements.count { it.isUnlocked }.toFloat() / achievements.size.toFloat()
                    Text(
                        text = "Общий прогресс: ${(progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp),
                    color = Color.Yellow,
                    trackColor = Color.Gray.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список достижений
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementCard(achievement = achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked)
                Color.Green.copy(alpha = 0.2f)
            else
                Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка достижения
            Text(
                text = if (achievement.isUnlocked) "🏆" else "🔒",
                fontSize = 32.sp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) Color.White else Color.Gray
                )
                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = if (achievement.isUnlocked) Color.Gray else Color.DarkGray
                )

                if (achievement.isUnlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Награда: ${achievement.reward} кредитов",
                        fontSize = 10.sp,
                        color = Color.Yellow
                    )
                }
            }

            if (achievement.isUnlocked) {
                Text(
                    text = "ПОЛУЧЕНО",
                    color = Color.Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
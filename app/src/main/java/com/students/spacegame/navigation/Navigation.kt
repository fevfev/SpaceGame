package com.students.spacegame.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.students.spacegame.screens.*

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate("menu") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("menu") {
            MenuScreen(
                onPlayClick = { navController.navigate("ship_select") },
                onLeaderboardClick = { navController.navigate("leaderboard") },
                onSettingsClick = { navController.navigate("settings") },
                onUpgradesClick = { navController.navigate("upgrades") },
                onAchievementsClick = { navController.navigate("achievements") }
            )
        }

        composable("ship_select") {
            ShipSelectScreen(
                onContinue = { navController.navigate("weapon_select") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("weapon_select") {
            WeaponSelectScreen(
                onStartGame = { navController.navigate("game") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("game") {
            GameScreen(
                onGameEnd = { score ->
                    navController.navigate("result/$score") {
                        popUpTo("menu") { inclusive = false }
                    }
                }
            )
        }

        composable("result/{score}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            ResultScreen(
                score = score,
                onPlayAgain = {
                    navController.navigate("ship_select") {
                        popUpTo("menu") { inclusive = false }
                    }
                },
                onMainMenu = {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }

        composable("leaderboard") {
            LeaderboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("upgrades") {
            UpgradeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("achievements") {
            AchievementsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}


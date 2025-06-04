package com.students.spacegame

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import com.students.spacegame.screens.MenuScreen

/**
 * Пример UI-теста для MenuScreen с использованием Jetpack Compose Testing.
 * Документация: https://developer.android.com/jetpack/compose/testing
 */
class MenuScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMenuButtonsDisplayedAndClickable() {
        var playClicked = false
        composeTestRule.setContent {
            MenuScreen(
                onPlayClick = { playClicked = true },
                onLeaderboardClick = {},
                onSettingsClick = {},
                onUpgradesClick = {},
                onAchievementsClick = {}
            )
        }
        // Проверяем, что кнопка "НОВАЯ ИГРА" отображается
        composeTestRule.onNodeWithText("НОВАЯ ИГРА").assertExists()
        // Проверяем, что клик по кнопке работает
        composeTestRule.onNodeWithText("НОВАЯ ИГРА").performClick()
        assert(playClicked)
    }
}


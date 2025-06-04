package com.students.spacegame

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.students.spacegame.screens.GameScreen
import org.junit.Rule
import org.junit.Test

/**
 * Пример UI-теста для GameScreen с использованием Jetpack Compose Testing.
 * Документация: https://developer.android.com/jetpack/compose/testing
 */
class GameScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPauseButtonDisplayedAndClickable() {
        // Для UI-тестов рекомендуется использовать уникальные contentDescription для иконок
        // Подробнее: https://developer.android.com/jetpack/compose/testing#find
        composeTestRule.setContent {
            GameScreen(
                onGameEnd = {},
                // Можно передать viewModel с нужным состоянием, если требуется
            )
        }
        // Проверяем, что кнопка с contentDescription "Пауза" существует
        composeTestRule.onNodeWithContentDescription("Пауза").assertExists()
        // Проверяем, что клик по кнопке "Пауза" работает (если есть callback)
        composeTestRule.onNodeWithContentDescription("Пауза").performClick()
        // Для проверки реакции можно использовать флаг или mockViewModel
    }
}


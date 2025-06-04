package com.students.spacegame

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.students.spacegame.models.GameState
import com.students.spacegame.models.WeaponType
import com.students.spacegame.viewmodels.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

/**
 * Пример юнит-теста для GameViewModel.
 * Подробнее о тестировании ViewModel: https://developer.android.com/topic/libraries/architecture/viewmodel#testing
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GameViewModel

    @Before
    fun setUp() {
        // Используем mock-объекты для зависимостей
        val gameScoreDao = mock<com.students.spacegame.database.GameScoreDao>()
        val supabaseRepository = mock<com.students.spacegame.supabase.SupabaseRepository>()
        val gameViewManager = mock<com.students.spacegame.viewmodels.GameViewManager>()
        val soundManager = mock<com.students.spacegame.components.SoundManager>()
        viewModel = GameViewModel(gameScoreDao, supabaseRepository, gameViewManager, soundManager)
    }

    @Test
    fun testComboScoreIncreases() = runTest {
        val initialScore = viewModel.gameState.value.score
        viewModel.onEnemyKilled()
        val afterFirstKill = viewModel.gameState.value.score
        viewModel.onEnemyKilled()
        val afterSecondKill = viewModel.gameState.value.score
        // Второе убийство должно дать больше очков за счёт комбо
        assert(afterSecondKill > afterFirstKill)
        assert(afterFirstKill > initialScore)
    }
}


package com.students.spacegame.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.students.spacegame.database.GameScore
import com.students.spacegame.database.GameScoreDao
import com.students.spacegame.supabase.SupabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val gameScoreDao: GameScoreDao,
    private val supabaseRepository: SupabaseRepository
) : ViewModel() {

    private val _topScores = MutableStateFlow<List<GameScore>>(emptyList())
    val topScores: StateFlow<List<GameScore>> = _topScores

    init {
        loadScores()
    }

    private fun loadScores() {
        viewModelScope.launch {
            // Сначала загружаем локальные данные
            gameScoreDao.getTopScores().collect { localScores ->
                _topScores.value = localScores
            }

            // Затем пытаемся загрузить с Supabase
            try {
                val remoteScores = supabaseRepository.getTopScores()
                if (remoteScores.isNotEmpty()) {
                    _topScores.value = remoteScores
                }
            } catch (e: Exception) {
                // Используем локальные данные при ошибке
            }
        }
    }
}

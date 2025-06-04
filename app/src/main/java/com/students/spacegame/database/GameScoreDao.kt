package com.students.spacegame.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameScoreDao {
    @Insert
    suspend fun insert(score: GameScore)

    @Query("SELECT * FROM game_scores ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<GameScore>>

    @Query("DELETE FROM game_scores")
    suspend fun deleteAll()

    @Query("SELECT * FROM game_scores WHERE playerName = :playerName ORDER BY score DESC LIMIT 1")
    suspend fun getBestScore(playerName: String): GameScore?

    @Update
    suspend fun update(score: GameScore)

    @Delete
    suspend fun delete(score: GameScore)

    @Query("SELECT COUNT(*) FROM game_scores")
    suspend fun getScoreCount(): Int
}

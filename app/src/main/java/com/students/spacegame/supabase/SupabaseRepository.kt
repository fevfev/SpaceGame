package com.students.spacegame.supabase

import com.students.spacegame.database.GameScore
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun saveScore(score: GameScore) {
        try {
            supabaseClient.client.from("game_scores").insert(score)
        } catch (e: Exception) {
        }
    }
    suspend fun getTopScores(): List<GameScore> {
        return try {
            val response = supabaseClient.client
                .from("game_scores")
                .select()
                .decodeList<GameScore>()
            response.sortedByDescending { it.score }.take(10)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
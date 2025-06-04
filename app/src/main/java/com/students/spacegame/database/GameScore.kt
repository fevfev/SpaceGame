package com.students.spacegame.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "game_scores")
@Serializable
data class GameScore(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerName: String,
    val score: Int,
    val level: Int,
    val date: Long = System.currentTimeMillis()
)

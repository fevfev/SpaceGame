package com.students.spacegame.models

data class Zone(
    val id: Int,
    val name: String,
    val requiredScore: Int,
    val backgroundImage: String,
    val musicTrack: String,
    val enemySpawnRate: Float,
    val enemySpeed: Float,
    val bossType: BossType?,
    val environmentalEffects: List<String>
) {
    companion object {
        fun getAllZones(): List<Zone> = listOf(
            Zone(
                id = 1,
                name = "Астероидный пояс",
                requiredScore = 0,
                backgroundImage = "bg_asteroid_belt.jpg",
                musicTrack = "music_zone1.mp3",
                enemySpawnRate = 0.02f,
                enemySpeed = 1.0f,
                bossType = null,
                environmentalEffects = listOf("rotating_asteroids")
            ),
            Zone(
                id = 2,
                name = "Туманность",
                requiredScore = 1000,
                backgroundImage = "bg_nebula.jpg",
                musicTrack = "music_zone2.mp3",
                enemySpawnRate = 0.025f,
                enemySpeed = 1.2f,
                bossType = null,
                environmentalEffects = listOf("fog_effect", "lightning_storms")
            ),
            Zone(
                id = 3,
                name = "Вражеский сектор",
                requiredScore = 2000,
                backgroundImage = "bg_enemy_sector.jpg",
                musicTrack = "music_zone3.mp3",
                enemySpawnRate = 0.03f,
                enemySpeed = 1.4f,
                bossType = BossType.DESTROYER,
                environmentalEffects = listOf("laser_barriers", "mine_fields")
            ),
            Zone(
                id = 4,
                name = "Черная дыра",
                requiredScore = 4000,
                backgroundImage = "bg_black_hole.jpg",
                musicTrack = "music_zone4.mp3",
                enemySpawnRate = 0.035f,
                enemySpeed = 1.6f,
                bossType = null,
                environmentalEffects = listOf("gravity_pull", "time_distortion")
            ),
            Zone(
                id = 5,
                name = "Финальная битва",
                requiredScore = 6000,
                backgroundImage = "bg_final_battle.jpg",
                musicTrack = "music_final_boss.mp3",
                enemySpawnRate = 0.04f,
                enemySpeed = 1.8f,
                bossType = BossType.MOTHERSHIP,
                environmentalEffects = listOf("explosions", "debris_rain")
            )
        )
    }
}

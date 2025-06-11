package com.students.spacegame.components


import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission
import com.students.spacegame.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var backgroundMusic: MediaPlayer? = null
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .build()

    private val sounds = mutableMapOf<String, Int>()
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    var soundEnabled = true
    var musicEnabled = true
    var vibrationEnabled = true

    init {
        loadSounds()
    }

    private fun loadSounds() {
        try {
            sounds["laser_shot"] = soundPool.load(context, R.raw.sound_laser_shot, 1)
            sounds["plasma_shot"] = soundPool.load(context, R.raw.sound_plasma_shot, 1)
            sounds["railgun_shot"] = soundPool.load(context, R.raw.sound_railgun_shot, 1)
            sounds["missile_shot"] = soundPool.load(context, R.raw.sound_missile_shot, 1)
            sounds["enemy_explosion"] = soundPool.load(context, R.raw.sound_enemy_explosion, 1)
            sounds["ship_select"] = soundPool.load(context, R.raw.sound_ship_select, 1)
            sounds["button_click"] = soundPool.load(context, R.raw.sound_button_click, 1)
            sounds["victory"] = soundPool.load(context, R.raw.sound_victory, 1)
            sounds["defeat"] = soundPool.load(context, R.raw.sound_defeat, 1)
            sounds["boss_spawn"] = soundPool.load(context, R.raw.sound_boss_spawn, 1)
            sounds["bonus_pickup"] = soundPool.load(context, R.raw.sound_bonus_pickup, 1)
        } catch (e: Exception) {
            println("Ошибка загрузки звуков: ${e.message}")
        }
    }

    fun playBackgroundMusic(zone: Int) {
        if (!musicEnabled) return

        try {
            backgroundMusic?.release()
            val musicRes = when (zone) {
                1 -> R.raw.music_zone1
                2 -> R.raw.music_zone2
                3 -> R.raw.music_zone3
                4 -> R.raw.music_zone4
                5 -> R.raw.music_boss
                else -> R.raw.music_menu
            }

            backgroundMusic = MediaPlayer.create(context, musicRes)?.apply {
                isLooping = true
                setVolume(0.7f, 0.7f)
                start()
            }
        } catch (e: Exception) {
            println("Ошибка воспроизведения музыки: ${e.message}")
        }
    }

    fun playSound(soundName: String, volume: Float = 1f) {
        if (!soundEnabled) return

        sounds[soundName]?.let { soundId ->
            soundPool.play(soundId, volume, volume, 1, 0, 1f)
        }
    }

    fun playWeaponSound(weaponType: com.students.spacegame.models.WeaponType) {
        val soundName = when (weaponType) {
            com.students.spacegame.models.WeaponType.LASER -> "laser_shot"
            com.students.spacegame.models.WeaponType.PLASMA -> "plasma_shot"
            com.students.spacegame.models.WeaponType.RAIL_GUN -> "railgun_shot"
            com.students.spacegame.models.WeaponType.MISSILE -> "missile_shot"
            else -> "laser_shot"
        }
        playSound(soundName)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun vibrate(duration: Long = 100) {
        if (!vibrationEnabled) return

        try {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            println("Ошибка вибрации: ${e.message}")
        }
    }

    fun stopMusic() {
        backgroundMusic?.pause()
    }

    fun resumeMusic() {
        if (musicEnabled) {
            backgroundMusic?.start()
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun updateMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        if (!enabled) {
            stopMusic()
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        vibrationEnabled = enabled
    }

    fun release() {
        backgroundMusic?.release()
        soundPool.release()
    }
}


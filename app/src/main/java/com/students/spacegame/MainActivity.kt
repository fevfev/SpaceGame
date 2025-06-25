package com.students.spacegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.students.spacegame.navigation.Navigation
import com.students.spacegame.ui.theme.SpaceGameTheme
import dagger.hilt.android.AndroidEntryPoint
import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import com.students.spacegame.di.SoundManagerEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val soundManager: com.students.spacegame.components.SoundManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            SoundManagerEntryPoint::class.java
        ).soundManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.stopMusic()
    }

    override fun onResume() {
        super.onResume()
        soundManager.resumeMusic()
    }
}
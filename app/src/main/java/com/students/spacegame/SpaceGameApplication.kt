package com.students.spacegame

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpaceGameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}

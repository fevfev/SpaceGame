package com.students.spacegame.di


import com.students.spacegame.components.SoundManager
import com.students.spacegame.viewmodels.GameViewManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GameViewManagerEntryPoint {
    fun gameViewManager(): GameViewManager
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SoundManagerEntryPoint {
    fun soundManager(): SoundManager
}

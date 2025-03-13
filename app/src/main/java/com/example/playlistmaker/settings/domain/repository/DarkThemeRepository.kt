package com.example.playlistmaker.settings.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import kotlinx.coroutines.flow.Flow

interface DarkThemeRepository {

    suspend fun getDarkTheme() : ConsumerData<Boolean>

    fun getDarkThemeFlow(): Flow<ConsumerData<Boolean>>

    suspend fun saveDarkTheme(darkTheme: Boolean)


}

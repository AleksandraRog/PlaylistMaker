package com.example.playlistmaker.settings.domain.interactors

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import kotlinx.coroutines.flow.Flow

interface DarkThemeInteractor {

    fun getDarkThemeFlow() : Flow<ConsumerData<Boolean>>

    suspend fun saveDarkTheme(darkTheme: Boolean)

    suspend fun getThemeSync() : ConsumerData<Boolean>


}

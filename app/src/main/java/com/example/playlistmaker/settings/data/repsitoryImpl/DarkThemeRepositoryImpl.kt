package com.example.playlistmaker.settings.data.repsitoryImpl

import com.example.playlistmaker.common.data.SharedPreferencesClient
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.data.dto.DarkThemeResponse
import com.example.playlistmaker.settings.data.local.DarkThemeManager
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DarkThemeRepositoryImpl(private val darkThemeManager: DarkThemeManager,  private val sharedPreferencesClient: SharedPreferencesClient,) :
    DarkThemeRepository {

    override suspend fun getDarkTheme(): ConsumerData<Boolean> {
        val darkThemeResponse = sharedPreferencesClient.doRequestSuspend()
        return if (darkThemeResponse is DarkThemeResponse) ConsumerData(darkThemeResponse.results) else {
            ConsumerData(false, darkThemeResponse.resultCode)
        }

    }

    override fun getDarkThemeFlow(): Flow<ConsumerData<Boolean>> = flow {
        val darkThemeResponse = sharedPreferencesClient.doRequestSuspend()
        emit(
            if (darkThemeResponse is DarkThemeResponse) ConsumerData(darkThemeResponse.results) else
                ConsumerData(false, darkThemeResponse.resultCode)
        )
    }

    override suspend fun saveDarkTheme(darkTheme: Boolean) {
        darkThemeManager.saveDataSuspend(darkTheme)
    }
}

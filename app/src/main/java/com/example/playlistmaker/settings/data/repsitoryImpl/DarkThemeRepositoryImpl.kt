package com.example.playlistmaker.settings.data.repsitoryImpl

import com.example.playlistmaker.common.data.SharedPreferencesClient
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.data.dto.DarkThemeResponse
import com.example.playlistmaker.settings.data.local.DarkThemeManager
import com.example.playlistmaker.settings.domain.repository.DarkThemeRepository

class DarkThemeRepositoryImpl(private val darkThemeManager: DarkThemeManager,  private val sharedPreferencesClient: SharedPreferencesClient,) :
    DarkThemeRepository {

    override fun getDarkTheme(): ConsumerData<Boolean> {
        val darkThemeResponse = sharedPreferencesClient.doRequest()
        return if (darkThemeResponse is DarkThemeResponse) ConsumerData(darkThemeResponse.results) else {
            ConsumerData(false, darkThemeResponse.resultCode)
        }

    }

    override fun saveDarkTheme(darkTheme: Boolean) {
        darkThemeManager.saveData(darkTheme)
    }
}

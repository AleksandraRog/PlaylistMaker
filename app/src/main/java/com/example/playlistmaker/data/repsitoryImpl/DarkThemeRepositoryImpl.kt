package com.example.playlistmaker.data.repsitoryImpl

import android.content.SharedPreferences
import com.example.playlistmaker.data.SharedPreferencesClient
import com.example.playlistmaker.data.dto.DarkThemeResponse
import com.example.playlistmaker.data.local.DarkThemeManager
import com.example.playlistmaker.data.local.LocalStorageManager
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer
import com.example.playlistmaker.domain.repository.DarkThemeRepository

class DarkThemeRepositoryImpl(
    private val sharedPreferencesClient: SharedPreferencesClient
) :
    DarkThemeRepository {

    override fun getDarkTheme(): ConsumerData<Boolean> {
        val darkThemeResponse = sharedPreferencesClient.doRequest()
        return if (darkThemeResponse is DarkThemeResponse) ConsumerData(darkThemeResponse.results) else {
            ConsumerData(false, darkThemeResponse.resultCode)
        }

    }

    override fun saveDarkTheme(darkTheme: Boolean) {
        DarkThemeManager.saveData(darkTheme)
    }

    override fun observeThemeChanges(lisenerConsumer: ListenerConsumer<Boolean>) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == DarkThemeManager.getDarkThemeKey()) {
                lisenerConsumer.consume(getDarkTheme())
            }
        }
        DarkThemeManager.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
}

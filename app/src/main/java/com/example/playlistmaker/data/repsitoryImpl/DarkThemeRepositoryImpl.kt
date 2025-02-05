package com.example.playlistmaker.data.repsitoryImpl

import android.content.SharedPreferences
import com.example.playlistmaker.data.local.DarkThemeManager
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer
import com.example.playlistmaker.domain.repository.DarkThemeRepository

class DarkThemeRepositoryImpl(private val darkThemeManager: DarkThemeManager) :
    DarkThemeRepository {

    override fun getDarkTheme(): ConsumerData<Boolean> {
        return ConsumerData(darkThemeManager.getData())
    }

    override fun saveDarkTheme(darkTheme: Boolean) {
        darkThemeManager.saveData(darkTheme)
    }

    override fun observeThemeChanges(lisenerConsumer: ListenerConsumer<Boolean>) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == darkThemeManager.getDarkThemeKey()) {
                lisenerConsumer.consume(getDarkTheme())
            }
        }
        darkThemeManager.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
}

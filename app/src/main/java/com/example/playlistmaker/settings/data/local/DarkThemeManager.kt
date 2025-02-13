package com.example.playlistmaker.settings.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.common.data.local.LocalStorageManager
import org.koin.java.KoinJavaComponent.getKoin

object DarkThemeManager : LocalStorageManager<Boolean?> {

    val sharedPreferences: SharedPreferences by lazy {
        getKoin().get<SharedPreferences>()
    }

    override fun getData(): Boolean? {
        return if (sharedPreferences.contains(DARK_THEME_KEY)) {
            sharedPreferences.getBoolean(DARK_THEME_KEY, false) // или true, если нужно
        } else {
            null
        }
    }

    override fun saveData(data: Boolean?) {
        if (data != null) {
            sharedPreferences.edit().putBoolean(DARK_THEME_KEY, data).apply()
        }
    }

    fun getDarkThemeKey() : String {
        return DARK_THEME_KEY
    }

    private val DARK_THEME_KEY = "DARK_THEME_KEY"
}

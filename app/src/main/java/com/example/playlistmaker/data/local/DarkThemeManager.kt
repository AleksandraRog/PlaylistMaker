package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import org.koin.java.KoinJavaComponent.getKoin

class DarkThemeManager : LocalStorageManager<Boolean> {

    val sharedPreferences: SharedPreferences by lazy {
        getKoin().get<SharedPreferences>()
    }

    override fun getData(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY,false)
    }

    override fun saveData(data: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY,data).apply()
    }

    fun getDarkThemeKey() : String {
        return DARK_THEME_KEY
    }

    private val DARK_THEME_KEY = "DARK_THEME_KEY"
}

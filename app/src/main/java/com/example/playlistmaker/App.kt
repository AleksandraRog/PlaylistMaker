package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App() : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        instance = this

        val preferences = getSharedPreferences(App.PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        darkTheme = preferences.getBoolean(DARK_THEME_KEY, false)

        switchTheme(darkTheme)
    }

    fun switchTheme(enableTheme: Boolean) {
        val themeToEnable = enableTheme
        AppCompatDelegate.setDefaultNightMode(
            if (themeToEnable) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        lateinit var instance: App
        const val PLAYLISTMAKER_PREFERENCES = "PLAYLIST_MAKER_PROJECT"
        const val DARK_THEME_KEY = "DARK_THEME_KEY"
        const val HISTORY_LIST_KEY = "HISTORY_LIST_KEY"
    }
}
